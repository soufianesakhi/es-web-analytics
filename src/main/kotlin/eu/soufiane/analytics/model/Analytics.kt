package eu.soufiane.analytics.model


data class Visitor(
  var ip: String,
  var date: String,
  var userAgent: String,
  var page: String?,
  var otherParameters: Map<String, String>
)

data class UserAgent(
  var device: String,
  var browser: String,
  var browserName: String,
  var os: String
)
