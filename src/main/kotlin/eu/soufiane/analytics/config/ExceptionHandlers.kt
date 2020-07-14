package eu.soufiane.analytics.config

import eu.soufiane.analytics.utils.badRequest
import eu.soufiane.analytics.utils.internalError
import kotlinx.coroutines.CoroutineExceptionHandler
import mu.KLogging
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotWritableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController


@ControllerAdvice
@RestController
class ExceptionHandlers {
  companion object : KLogging()

  @ExceptionHandler(HttpMessageNotWritableException::class)
  fun handleHttpMessageNotWritableException(ex: HttpMessageNotWritableException): ResponseEntity<Any> {
    logger.error { """Failed to write HTTP message: $ex""" }
    return badRequest()
  }

  @ExceptionHandler(Exception::class)
  fun handleException(ex: Exception): ResponseEntity<Any> {
    logger.error(ex) { """$ex""" }
    return badRequest()
  }

  @ExceptionHandler(Throwable::class)
  fun handleThrowable(): ResponseEntity<Any> = internalError()
}

val KLogging.COROUTINE_EXCEPTION_HANDLER: CoroutineExceptionHandler
  get() = CoroutineExceptionHandler { _, exception ->
    logger.error(exception) { "Error while executing a coroutine" }
  }
