package eu.soufiane.analytics.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import io.quarkus.jackson.ObjectMapperCustomizer
import javax.inject.Singleton

@Singleton
class JsonConfig : ObjectMapperCustomizer {
  override fun customize(mapper: ObjectMapper) {
    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
  }
}
