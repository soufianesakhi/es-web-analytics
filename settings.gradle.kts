pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    gradlePluginPortal()
  }
  val kotlinVersion: String by settings
  val quarkusPluginVersion: String by settings
  plugins {
    id("io.quarkus") version quarkusPluginVersion
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
  }
}
rootProject.name = "es-web-analytics"
