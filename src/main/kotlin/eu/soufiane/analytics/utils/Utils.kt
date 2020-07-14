package eu.soufiane.analytics.utils

import inet.ipaddr.IPAddressString
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import java.net.InetAddress
import kotlin.math.ceil

class Utils {
  companion object : KLogging()
}

fun Long.toMegabytes(): Long {
  return ceil(this / 1_000_000.0f).toLong()
}

fun <T> badRequest(): ResponseEntity<T> = ResponseEntity.badRequest().build()
fun <T> internalError(): ResponseEntity<T> = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()

fun ServerHttpRequest.getHeader(name: String): String? =
  headers[name]?.first()

val ServerHttpRequest.remoteAddr: String
  get() = remoteAddress?.address?.hostAddress ?: ""

fun ipToInetAddress(ip: String): InetAddress =
  IPAddressString(ip).address?.toInetAddress() ?: InetAddress.getLoopbackAddress().also {
    Utils.logger.warn { "invalid ip: ($ip)" }
  }
