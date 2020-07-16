package eu.soufiane.analytics.model

import io.quarkus.runtime.annotations.RegisterForReflection


@RegisterForReflection
data class Visitor(
  var ip: String,
  var date: String,
  var userAgent: String,
  var page: String?,
  var otherParameters: Map<String, String>
)

@RegisterForReflection
data class UserAgent(
  var device: String,
  var browser: String,
  var browserName: String,
  var os: String
)
