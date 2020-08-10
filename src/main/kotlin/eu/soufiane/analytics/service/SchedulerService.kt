package eu.soufiane.analytics.service

import eu.soufiane.analytics.utils.toMegabytes
import io.quarkus.scheduler.Scheduled
import org.eclipse.microprofile.config.inject.ConfigProperty
import java.lang.management.ManagementFactory
import java.lang.management.MemoryType
import java.lang.management.MemoryUsage
import java.time.Instant
import java.util.*
import javax.inject.Singleton

@Singleton
class SchedulerService(private val indexService: IndexService) {

  @JvmField
  @ConfigProperty(name = "monitoring.performance.enabled")
  var performanceMonitoringEnabled = false

  @Scheduled(every = "5m")
  fun performanceMonitoring() {
    if (!performanceMonitoringEnabled) {
      return
    }
    val metrics = LinkedHashMap<String, Any>()
    ManagementFactory.getMemoryMXBean().run {
      collectMemoryMetrics(metrics, heapMemoryUsage, MemoryType.HEAP.name)
      collectMemoryMetrics(metrics, nonHeapMemoryUsage, MemoryType.NON_HEAP.name)
    }
    metrics["systemLoadAverage"] = ManagementFactory.getOperatingSystemMXBean().systemLoadAverage
    metrics["threadCount"] = ManagementFactory.getThreadMXBean().threadCount
    metrics["totalLoadedClassCount"] = ManagementFactory.getClassLoadingMXBean().totalLoadedClassCount
    collectGcMetrics(metrics)
    val instant = Instant.now()
    metrics["timestamp"] = instant.toEpochMilli()
    indexService.performanceDAOs.forEach {
      it.index(instant.toString(), metrics)
    }
  }

  private fun collectGcMetrics(metrics: LinkedHashMap<String, Any>) {
    var totalGarbageCollectionCount: Long = 0
    var totalGarbageCollectionTime: Long = 0
    for (gc in ManagementFactory.getGarbageCollectorMXBeans()) {
      val count = gc.collectionCount
      if (count >= 0) {
        totalGarbageCollectionCount += count
      }
      val time = gc.collectionTime
      if (time >= 0) {
        totalGarbageCollectionTime += time
      }
    }
    metrics["totalGcCount"] = totalGarbageCollectionCount
    metrics["totalGcTime"] = totalGarbageCollectionTime
  }

  private fun collectMemoryMetrics(metrics: LinkedHashMap<String, Any>, usage: MemoryUsage, name: String) {
    metrics["usedMemory_$name"] = usage.used.toMegabytes()
    metrics["committedMemory_$name"] = usage.committed.toMegabytes()
    metrics["maxMemory_$name"] = usage.max.toMegabytes()
  }
}
