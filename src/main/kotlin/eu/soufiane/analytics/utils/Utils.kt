package eu.soufiane.analytics.utils

import inet.ipaddr.IPAddressString
import mu.KLogging
import org.jboss.resteasy.spi.HttpRequest
import java.net.InetAddress
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.core.Response
import kotlin.math.ceil

class Utils {
  companion object : KLogging()
}

fun Long.toMegabytes(): Long {
  return ceil(this / 1_000_000.0f).toLong()
}

fun ok(body: Any? = ""): Response = Response.ok(body).build()
fun badRequest(): Response = Response.status(Response.Status.BAD_REQUEST).build()
fun internalError(): Response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build()

fun HttpRequest.getHeader(name: String): String? = httpHeaders.getHeaderString(name)

fun <K, V> MultivaluedMap<K, V>.toSingleValueMap(): Map<K, V> {
  val map = HashMap<K, V>(size)
  keys.forEach { key ->
    map[key] = getFirst(key)
  }
  return map
}

fun ipToInetAddress(ip: String): InetAddress =
  IPAddressString(ip).address?.toInetAddress() ?: InetAddress.getLoopbackAddress().also {
    Utils.logger.warn { "invalid ip: ($ip)" }
  }
