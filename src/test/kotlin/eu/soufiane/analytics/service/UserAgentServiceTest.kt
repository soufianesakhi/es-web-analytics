package eu.soufiane.analytics.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class UserAgentServiceTest {
  private val uaService = UserAgentService()

  @Test
  fun test_mobile_chrome() {
    val ua = "Mozilla/5.0 (Linux; Android 10; Mi 9T Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.106 Mobile Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("Phone", result.device)
    assertEquals("Chrome 83", result.browser)
    assertEquals("Chrome", result.browserName)
    assertEquals("Android 10", result.os)
  }

  @Test
  fun test_mobile_safari() {
    val ua = "Mozilla/5.0 (iPhone; CPU iPhone OS 13_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.1.1 Mobile/15E148 Safari/604.1"
    val result = uaService.parse(ua)
    assertEquals("Phone", result.device)
    assertEquals("Safari 13", result.browser)
    assertEquals("Safari", result.browserName)
    assertEquals("iOS 13.5.1", result.os)
  }

  @Test
  fun test_mobile_firefox() {
    val ua = "Mozilla/5.0 (Android 8.0.0; Mobile; rv:68.0) Gecko/68.0 Firefox/68.0"
    val result = uaService.parse(ua)
    assertEquals("Phone", result.device)
    assertEquals("Firefox 68", result.browser)
    assertEquals("Firefox", result.browserName)
    assertEquals("Android 8.0.0", result.os)
  }

  @Test
  fun test_desktop_windows() {
    val ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("Desktop", result.device)
    assertEquals("Windows 10.0", result.os)
  }

  @Test
  fun test_desktop_macos() {
    val ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("Desktop", result.device)
    assertEquals("Mac OS X 10.14.6", result.os)
  }

  @Test
  fun test_desktop_linux() {
    val ua = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/83.0.4103.116 Chrome/83.0.4103.116 Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("Desktop", result.device)
    assertTrue(result.os.startsWith("Linux"))
  }

  @Test
  fun test_tablet_ipad() {
    val ua = "Mozilla/5.0 (iPad; CPU OS 13_5_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.1 Mobile/15E148 Safari/604.1"
    val result = uaService.parse(ua)
    assertEquals("Tablet", result.device)
  }

  @Test
  fun test_tablet_sm() {
    val ua = "Mozilla/5.0 (Linux; Android 8.1.0; SAMSUNG SM-T580) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/12.0 Chrome/79.0.3945.136 Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("Tablet", result.device)
  }

  @Test
  fun test_crawler_google() {
    val ua = "Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1;  http://www.google.com/bot.html) Chrome/83.0.4103.118 Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("Crawler", result.device)
    assertEquals("Googlebot 2", result.browser)
  }

  @Test
  fun test_crawler_google_mobile() {
    val ua = "Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.118 Mobile Safari/537.36 (compatible; Googlebot/2.1;  http://www.google.com/bot.html)"
    val result = uaService.parse(ua)
    assertEquals("Crawler", result.device)
    assertEquals("Googlebot 2", result.browser)
  }

  @Test
  fun test_crawler_bing() {
    val ua = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534  (KHTML, like Gecko) BingPreview/1.0b"
    val result = uaService.parse(ua)
    assertEquals("Crawler", result.device)
    assertEquals("Windows 7", result.os)
    assertEquals("BingPreview 1", result.browser)
  }

  @Test
  fun test_tv_1() {
    val ua = "Mozilla/5.0 (SMART-TV; Linux; Tizen 5.0) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/2.2 Chrome/63.0.3239.84 TV Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("TV", result.device)
  }

  @Test
  fun test_tv_2() {
    val ua = "Mozilla/5.0 (Linux; NetCast; U) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/38.0.2125.122 Safari/537.31 SmartTV/7.5"
    val result = uaService.parse(ua)
    assertEquals("TV", result.device)
  }

  @Test
  fun test_postman() {
    val ua = "PostmanRuntime/7.3.0"
    val result = uaService.parse(ua)
    assertEquals("Unknown", result.device)
    assertEquals("Unknown ??", result.os)
    assertEquals("PostmanRuntime 7", result.browser)
    assertEquals("PostmanRuntime", result.browserName)
  }

  @Test
  fun test_unkown() {
    val ua = "Mozilla/5.0 (en-US) AppleWebKit/537.36 (KHTML, like Gecko; Widget Server) Chrome/83.0.4103.118 Safari/537.36"
    val result = uaService.parse(ua)
    assertEquals("Unknown", result.device)
    assertEquals("Unknown ??", result.os)
  }

  @Test
  fun test_neoload() {
    val ua = "neoload"
    val result = uaService.parse(ua)
    assertEquals("Unknown", result.device)
    assertEquals("Unknown ??", result.os)
    assertEquals("neoload", result.browser)
    assertEquals("neoload", result.browserName)
  }

  @Test
  fun test_googleweblight() {
    val ua = "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko; googleweblight) Chrome/38.0.1025.166 Mobile Safari/535.19"
    val result = uaService.parse(ua)
    assertEquals("Phone", result.device)
  }
}
