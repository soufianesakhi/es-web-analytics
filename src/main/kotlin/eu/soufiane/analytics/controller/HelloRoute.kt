package eu.soufiane.analytics.controller

import io.quarkus.vertx.web.Route
import io.vertx.core.http.HttpMethod
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class HelloRoute {
  @Route(path = "/hello-route", methods = [HttpMethod.GET], produces = ["text/plain;charset=UTF-8"])
  fun hello() = "hello"
}
