package eu.soufiane.analytics.controller

import eu.soufiane.analytics.config.COROUTINE_EXCEPTION_HANDLER
import eu.soufiane.analytics.model.Visitor
import eu.soufiane.analytics.service.AnalyticsService
import eu.soufiane.analytics.utils.getHeader
import eu.soufiane.analytics.utils.ok
import eu.soufiane.analytics.utils.toSingleValueMap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KLogging
import org.jboss.resteasy.spi.HttpRequest
import java.time.Instant
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("pageview")
@Produces(MediaType.APPLICATION_JSON)
class AnalyticsController(val analyticsService: AnalyticsService) {

  companion object : KLogging()

  @Context
  lateinit var request: HttpRequest

  @POST
  fun postPageView(): Response = getPageView()

  @GET
  fun getPageView(): Response {
    val visitor = request.getVisitor()
    GlobalScope.launch(COROUTINE_EXCEPTION_HANDLER) {
      analyticsService.pageView(visitor)
    }
    return ok()
  }

  @GET
  @Path("dryrun")
  fun getPageViewDryRun(): Response {
    return runBlocking {
      val pageView = analyticsService.pageView(request.getVisitor(), true)
      ok(pageView)
    }
  }

  private fun HttpRequest.getVisitor() = Visitor(
    ip = remoteAddress,
    date = Instant.now().toString(),
    userAgent = getHeader("user-agent") ?: "",
    page = getHeader("referer"),
    otherParameters = uri.queryParameters.toSingleValueMap()
  )

}
