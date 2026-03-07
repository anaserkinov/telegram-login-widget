package me.anasmusa.shared

import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.text.buildString

actual suspend fun getButtonHtml(config: TelegramLoginConfig): String? =
    withContext(Dispatchers.IO) {
        try {
            val connection =
                URL(
                    buildString {
                        append("https://oauth.telegram.org/embed/${config.botUsername}?")
                        append("origin=${config.websiteUrl}&")
                        append("return_to=${config.websiteUrl}&")
                        append("lang=${config.languageCode}")
                    },
                ).openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "GET"
                connection.doOutput = true
                connection.setRequestProperty("Accept", "text/html")

                val cookies = getTelegramCookies()
                if (cookies != null) {
                    connection.setRequestProperty("Cookie", cookies)
                }

                val responseCode = connection.responseCode
                if (responseCode in 200..299) {
                    val stream = connection.inputStream
                    stream.bufferedReader().use { it.readText() }
                } else {
                    null
                }
            } finally {
                connection.disconnect()
            }
        } catch (_: Exception) {
            null
        }
    }

actual suspend fun loadImage(url: String): ByteArray? =
    withContext(Dispatchers.IO) {
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            try {
                connection.connect()

                val bytes =
                    connection.getInputStream().use { input ->
                        input.readBytes()
                    }

                bytes
            } finally {
                connection.disconnect()
            }
        } catch (e: Exception) {
            null
        }
    }

actual suspend fun getTelegramCookies(): String? =
    CookieManager
        .getInstance()
        .getCookie("https://oauth.telegram.org")

actual suspend fun clearTelegramCookies(): Unit =
    withContext(Dispatchers.IO) {
        val cookieManager = CookieManager.getInstance()
        val url = "https://oauth.telegram.org"

        val expired = "Thu, 01 Jan 1970 00:00:00 GMT"

        cookieManager.setCookie(url, "stel_ssid=; Expires=$expired; Path=/")
        cookieManager.setCookie(url, "stel_acid=; Expires=$expired; Path=/")
        cookieManager.setCookie(url, "stel_token=; Expires=$expired; Path=/")

        cookieManager.flush()
    }
