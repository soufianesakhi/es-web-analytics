package eu.soufiane.analytics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class WebApplication

fun main(args: Array<String>) {
  runApplication<WebApplication>(*args)
}
