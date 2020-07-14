package eu.soufiane.analytics.model

data class ESClientConfig(
  val username: String,
  val password: String,
  val hostname: String,
  val port: Int,
  val scheme: String,
  val index: String
)

@Suppress("unused")
enum class ElasticsearchCategory {
  PAGE_VIEWS, PERFORMANCE;

  companion object {
    fun fromName(name: String) = values().first { name.toUpperCase() == it.name }
  }
}
