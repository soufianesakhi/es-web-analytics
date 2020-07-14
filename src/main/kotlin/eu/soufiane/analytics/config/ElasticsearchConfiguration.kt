package eu.soufiane.analytics.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import eu.soufiane.analytics.model.ESClientConfig
import eu.soufiane.analytics.model.ElasticsearchCategory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

typealias EsClientsMap = EnumMap<ElasticsearchCategory, MutableList<ESClientConfig>>

@Configuration
class ElasticsearchConfiguration {
  @Value("\${elasticsearch.config-file}")
  lateinit var filePath: String

  private val esClients: EsClientsMap by lazy {
    init()
  }

  private fun init(): EsClientsMap {
    val jsonConfig = File(filePath).apply {
      if (!exists()) {
        throw IllegalStateException("$ERROR_PREFIX: config file not found in '$filePath'")
      }
    }.readText()
    val esClients = EsClientsMap(ElasticsearchCategory::class.java)
    val jsonNode = ObjectMapper().readTree(jsonConfig)
    jsonNode.fieldNames().forEach { categoryName ->
      val categoryNode = jsonNode.get(categoryName)
      if (categoryNode is ArrayNode) {
        categoryNode.forEach {
          val esClient = ESClientConfig(
            username = it.getOrError("username").asText(),
            password = it.getOrError("password").asText(),
            hostname = it.getOrError("hostname").asText(),
            port = it.getOrError("port").asInt(),
            scheme = it.getOrError("scheme").asText(),
            index = it.getOrError("index").asText()
          )
          val category = ElasticsearchCategory.fromName(categoryName)
          esClients.getOrPut(category) { ArrayList() }.add(esClient)
        }
      }
    }
    return esClients
  }

  fun get(category: ElasticsearchCategory) = esClients[category]
}

private fun JsonNode.getOrError(field: String) = get(field) ?: {
  throw IllegalStateException("$ERROR_PREFIX: '$field' is required")
}()

const val ERROR_PREFIX = "Elasticsearch configuration error"
