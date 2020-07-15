package eu.soufiane.analytics.service

import com.fasterxml.jackson.databind.ObjectMapper
import eu.soufiane.analytics.config.ElasticsearchConfiguration
import eu.soufiane.analytics.model.ESClientConfig
import eu.soufiane.analytics.model.ElasticsearchCategory
import io.inbot.eskotlinwrapper.IndexDAO
import io.inbot.eskotlinwrapper.JacksonModelReaderAndWriter
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCredentialsProvider
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.crudDao
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Singleton
class IndexService(esConfiguration: ElasticsearchConfiguration) {
  @JvmField
  @ConfigProperty(name = "elasticsearch.index.append-date")
  var appendIndexDate = false

  val pageViewsDAOs by lazy {
    esConfiguration.get(ElasticsearchCategory.PAGE_VIEWS)!!.toDAO<Map<String, String>>()
  }
  val performanceDAOs by lazy {
    esConfiguration.get(ElasticsearchCategory.PERFORMANCE)!!.toDAO<Map<String, Any>>()
  }

  private inline fun <reified T : Any> List<ESClientConfig>.toDAO(): List<IndexDAO<T>> = map { config ->
    val credentialsProvider = BasicCredentialsProvider()
    credentialsProvider.setCredentials(
      AuthScope.ANY, UsernamePasswordCredentials(config.username, config.password)
    )
    val restClientBuilder = RestClient.builder(
      HttpHost(
        config.hostname,
        config.port,
        config.scheme
      )
    ).setHttpClientConfigCallback { httpClientBuilder ->
      val requestConfig: RequestConfig = RequestConfig.custom()
        .setConnectTimeout(15000)
        .setSocketTimeout(15000)
        .setConnectionRequestTimeout(15000)
        .build()
      httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        .setDefaultRequestConfig(requestConfig)
        .setMaxConnPerRoute(30)
        .setMaxConnTotal(30)
    }

    val esClient = RestHighLevelClient(restClientBuilder)
    val indexName = if (appendIndexDate) {
      val indexSuffix = SimpleDateFormat("yyyy-MM-dd").format(Date())
      "${config.index}-$indexSuffix"
    } else {
      config.index
    }
    esClient.crudDao(
      index = indexName,
      refreshAllowed = true,
      modelReaderAndWriter = JacksonModelReaderAndWriter(
        T::class,
        ObjectMapper().findAndRegisterModules()
      )
    )
  }
}
