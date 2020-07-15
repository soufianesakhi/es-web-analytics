import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("io.quarkus")
  kotlin("jvm")
  kotlin("plugin.allopen")
}

group = "eu.soufiane"
version = "2.0.0"

val javaVersion = JavaVersion.VERSION_11
val elasticsearchVersion: String by project
val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://jitpack.io")
}

dependencies {
  implementation("io.quarkus:quarkus-kotlin")
  implementation("io.quarkus:quarkus-scheduler")
  implementation("io.quarkus:quarkus-resteasy-jackson")
  implementation(platform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))

  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.6")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("com.google.guava:guava:27.0-jre")
  implementation("io.github.microutils:kotlin-logging:1.6.10")
  implementation("com.maxmind.geoip2:geoip2:2.12.0")
  implementation("com.github.ua-parser:uap-java:1.4.3") // https://github.com/ua-parser/uap-java
  implementation("is.tagomor.woothee:woothee-java:1.11.0") // https://github.com/woothee/woothee-java
  implementation("com.github.jillesvangurp:es-kotlin-wrapper-client:v0.12.0") // https://github.com/jillesvangurp/es-kotlin-wrapper-client
  implementation("com.github.seancfoley:ipaddress:5.3.1") // https://github.com/seancfoley/IPAddress

  constraints {
    listOf(
      "org.elasticsearch:elasticsearch",
      "org.elasticsearch.client:elasticsearch-rest-high-level-client",
      "org.elasticsearch.client:elasticsearch-rest-client"
    ).forEach {
      implementation(it) {
        version {
          strictly(elasticsearchVersion)
        }
      }
    }
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    jvmTarget = javaVersion.toString()
    javaParameters = true
  }
}

quarkus {
  setOutputDirectory("$projectDir/build/classes/kotlin/main")
  setWorkingDir("$projectDir")
}

allOpen {
  annotation("javax.ws.rs.Path")
  annotation("javax.enterprise.context.ApplicationScoped")
  annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.quarkusDev {
  setSourceDir("$projectDir/src/main/kotlin")
}

tasks.test {
  systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
}
