package eu.soufiane.analytics

import io.quarkus.runtime.Quarkus
import io.quarkus.runtime.annotations.QuarkusMain

@QuarkusMain
class WebApplication {
  companion object {
    @JvmStatic
    fun main(args: Array<String>) {
      Quarkus.run(*args)
    }
  }
}
