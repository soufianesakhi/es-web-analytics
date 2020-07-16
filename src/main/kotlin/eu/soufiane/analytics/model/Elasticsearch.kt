package eu.soufiane.analytics.model

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class ESClientConfig(
  val username: String,
  val password: String,
  val hostname: String,
  val port: Int,
  val scheme: String,
  val index: String
)

@RegisterForReflection
enum class ElasticsearchCategory {
  PAGE_VIEWS, PERFORMANCE;

  companion object {
    fun fromName(name: String) = values().first { name.toUpperCase() == it.name }
  }
}
