package eu.soufiane.analytics.controller

import eu.soufiane.analytics.config.COROUTINE_EXCEPTION_HANDLER
import eu.soufiane.analytics.model.Visitor
import eu.soufiane.analytics.service.AnalyticsService
import eu.soufiane.analytics.utils.getHeader
import eu.soufiane.analytics.utils.remoteAddr
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant


@RestController
class AnalyticsController(val analyticsService: AnalyticsService) {

  companion object : KLogging()

  @PostMapping("pageview")
  fun postPageView(request: ServerHttpRequest): ResponseEntity<Void> = getPageView(request)

  @GetMapping("pageview")
  fun getPageView(request: ServerHttpRequest): ResponseEntity<Void> {
    GlobalScope.launch(COROUTINE_EXCEPTION_HANDLER) {
      analyticsService.pageView(request.getVisitor())
    }
    return ResponseEntity.ok().build()
  }

  @GetMapping("pageview/dryrun")
  fun getPageViewDryRun(request: ServerHttpRequest): ResponseEntity<Map<String, String>> {
    return runBlocking {
      val pageView = analyticsService.pageView(request.getVisitor(), true)
      ResponseEntity.ok(pageView)
    }
  }

  private fun ServerHttpRequest.getVisitor() = Visitor(
    ip = remoteAddr,
    date = Instant.now().toString(),
    userAgent = getHeader("user-agent") ?: "",
    page = getHeader("referer"),
    otherParameters = queryParams.toSingleValueMap()
  )

}
