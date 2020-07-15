package eu.soufiane.analytics.config

import eu.soufiane.analytics.utils.badRequest
import eu.soufiane.analytics.utils.internalError
import kotlinx.coroutines.CoroutineExceptionHandler
import mu.KLogging
import javax.ws.rs.NotFoundException
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Response
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider


@Provider
class UncaughtThrowableExceptionMapper : ExceptionMapper<Throwable?> {
  companion object : KLogging()
  override fun toResponse(t: Throwable?): Response {
    return when(t) {
      is WebApplicationException -> t.response
      is Exception -> badRequest().also {
        logger.error(t) { "Uncaught exception" }
      }
      else -> internalError().also {
        logger.error(t) { "Internal Error" }
      }
    }
  }
}

@Provider
class NotFoundExceptionMapper: ExceptionMapper<NotFoundException> {
  override fun toResponse(exception: NotFoundException?): Response {
    return badRequest()
  }
}

val KLogging.COROUTINE_EXCEPTION_HANDLER: CoroutineExceptionHandler
  get() = CoroutineExceptionHandler { _, exception ->
    logger.error(exception) { "Error while executing a coroutine" }
  }
