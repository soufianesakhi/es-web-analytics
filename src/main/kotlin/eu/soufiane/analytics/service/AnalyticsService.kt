package eu.soufiane.analytics.service

import com.google.common.net.InternetDomainName
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import com.maxmind.geoip2.model.CountryResponse
import eu.soufiane.analytics.model.Visitor
import eu.soufiane.analytics.utils.ipToInetAddress
import kotlinx.coroutines.*
import mu.KLogging
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.net.URI
import java.util.*
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsService(private val indexService: IndexService) {
  companion object : KLogging()

  @JvmField
  @ConfigProperty(name = "analytics.include-ip")
  var includeIp = false

  @Inject
  lateinit var userAgentService: UserAgentService

  private val ipCountryDbReader: DatabaseReader
  private val ipReaderContext: ExecutorCoroutineDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()

  init {
    val ipCountryDatabase = AnalyticsService::class.java.getResourceAsStream("/GeoLite2-Country.mmdb")
    ipCountryDbReader = DatabaseReader.Builder(ipCountryDatabase).build()
  }

  suspend fun pageView(data: Visitor, dryRun: Boolean = false): Map<String, String> {
    val dto = LinkedHashMap<String, String>()
    val otherParameters = data.otherParameters
    dto["date"] = data.date
    dto["page"] = data.page ?: ""
    (otherParameters["referer"] ?: "").let { referer ->
      dto["referer"] = referer
      dto["refererDomain"] = parseRefererDomain(referer)
    }
    val ip = otherParameters["ip"] ?: data.ip
    dto["ip"] = ip
    try {
      parseIp(ip)?.let { (country, continent) ->
        dto["country"] = country
        dto["continent"] = continent
      }
      val ua = userAgentService.parse(data.userAgent)
      dto["device"] = ua.device
      dto["browserName"] = ua.browserName
      dto["browser"] = ua.browser
      dto["os"] = ua.os
    } catch (e: Exception) {
      dto["browser"] = data.userAgent
      logger.error(e) {
        "Error while parsing analytics data"
      }
    }
    dto.putAll(otherParameters)
    if (!includeIp) {
      dto.remove("ip")
    }
    if (dryRun) {
      return dto
    }
    val id = generateId()
    val deferreds = indexService.pageViewsDAOs.map {
      GlobalScope.async { it.index(id, dto) }
    }.toTypedArray()
    try {
      awaitAll(*deferreds)
    } catch (e: Exception) {
      logger.error(e) {
        "Error while sending data to index: ${e.message}\n$dto"
      }
    }
    return dto
  }

  private suspend fun parseIp(ip: String): Pair<String, String>? {
    if (ip == "127.0.0.1") {
      return null
    }
    return withContext(ipReaderContext) {
      try {
        val countryRes = parseCountry(ip)
        val country = countryRes.country.name
        val continent = countryRes.continent.name
        Pair(country, continent)
      } catch (e: AddressNotFoundException) {
        null
      }
    }
  }

  private fun parseCountry(ip: String): CountryResponse = ipCountryDbReader.country(ipToInetAddress(ip))

  private fun generateId() = UUID.randomUUID().toString()

  private fun parseRefererDomain(referer: String): String {
    return try {
      if (referer.startsWith("android-app://")) {
        return referer
      }
      val domain = URI(referer).host?.toLowerCase() ?: return ""
      if (domain.startsWith("www.google.")) {
        return "www.google.com"
      }
      @Suppress("UnstableApiUsage")
      return InternetDomainName.from(domain).run {
        if (isPublicSuffix) {
          topPrivateDomain().toString()
        } else {
          domain
        }
      }
    } catch (e: Exception) {
      logger.error { "Error while parsing the referer domain for: $referer" }
      ""
    }
  }
}
