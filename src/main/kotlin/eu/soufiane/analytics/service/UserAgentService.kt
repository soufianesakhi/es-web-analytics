package eu.soufiane.analytics.service

import `is`.tagomor.woothee.Classifier
import `is`.tagomor.woothee.DataSet.*
import eu.soufiane.analytics.model.UserAgent
import ua_parser.Client
import ua_parser.Parser
import javax.inject.Singleton

@Singleton
class UserAgentService {
  companion object {
    private val parser = Parser()

    @Suppress("unused")
    private val WOOTHEE_INITIALIZED = get("Win10") != null
    private val IPAD_REGEX = Regex("\\(\\s*ipad", RegexOption.IGNORE_CASE)
    private val VERSION_CLEANUP_REGEX = Regex("[a-z]+\\s*", RegexOption.IGNORE_CASE)
    private val VERSION_REGEX = Regex("(\\d+)(?:\\.\\d+(?:\\.\\d+)?)?\$")
    private const val UNKOWN_OS = "Unknown ??"
    private const val UNKOWN_DEVICE = "Unknown"
  }

  fun parse(userAgent: String): UserAgent = parser.parse(userAgent).let { ua ->
    val uaMap = Classifier.parse(userAgent)
    val (browserName, browser) = parseBrowser(uaMap, ua, userAgent)
    UserAgent(
      device = getDevice(userAgent, ua, uaMap),
      browserName = browserName,
      browser = browser,
      os = getOsNameVersion(uaMap, ua)
    )
  }

  private fun parseBrowser(
    uaMap: MutableMap<String, String>,
    ua: Client,
    userAgent: String
  ): Pair<String, String> {
    var browserName = uaMap[ATTRIBUTE_NAME] ?: ua.userAgent.family
    val browser = if (browserName == VALUE_UNKNOWN) {
      browserName = parseUnknownBrowserName(userAgent)
      VERSION_REGEX.find(userAgent)?.groupValues?.get(1)?.let {
        "$browserName $it"
      } ?: userAgent
    } else {
      "$browserName ${ua.userAgent.major}"
    }
    return Pair(browserName, browser)
  }

  private fun parseUnknownBrowserName(userAgent: String): String {
    val i = userAgent.lastIndexOf("/")
    return if (i > -1) {
      userAgent.substring(0, i).split(" ").last()
    } else {
      userAgent
    }
  }

  private fun getDevice(
    uaString: String,
    ua: Client,
    uaMap: MutableMap<String, String>
  ) = uaMap[ATTRIBUTE_CATEGORY]?.let { device ->
    if (device.toLowerCase().contains("phone")) {
      if (IPAD_REGEX.containsMatchIn(uaString) || !uaString.toLowerCase().contains(" mobile")) {
        "Tablet"
      } else {
        "Phone"
      }
    } else {
      when {
        device == VALUE_UNKNOWN -> UNKOWN_DEVICE
        uaString.contains("SmartTV", ignoreCase = true) || ua.device.family.contains("TV", ignoreCase = true) -> "TV"
        device == "pc" -> "Desktop"
        else -> device.capitalize()
      }
    }
  } ?: ua.device.family

  private fun getOsNameVersion(uaMap: Map<String, String>, ua: Client): String {
    val os = ua.os.family
    val version = uaMap[ATTRIBUTE_OS_VERSION]?.replace(VERSION_CLEANUP_REGEX, "") ?: ua.os.run {
      major?.let {
        StringBuilder(major).apply {
          minor?.let { append(".$it") }
          patch?.let { append(".$it") }
        }.toString()
      }
    }
    if (os == "Other") {
      return UNKOWN_OS
    }
    return StringBuilder(os).apply {
      version?.let { append(" $it") }
    }.toString()
  }
}
