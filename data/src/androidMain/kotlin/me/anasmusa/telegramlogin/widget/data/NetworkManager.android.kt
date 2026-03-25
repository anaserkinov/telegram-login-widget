package me.anasmusa.telegramlogin.widget.data

import android.webkit.CookieManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.ref.SoftReference

private var client: SoftReference<OkHttpClient>? = null

private fun getClient() =
    client?.get() ?: run {
        val new = OkHttpClient()
        client = SoftReference(new)
        new
    }

actual suspend fun getButtonHtml(config: TelegramLoginConfig): String? =
    withContext(Dispatchers.IO) {
        try {
            val client = getClient()

            val request =
                Request
                    .Builder()
                    .url(
                        buildString {
                            append("https://oauth.telegram.org/embed/${config.botUsername}?")
                            append("origin=${config.websiteUrl}&")
                            append("return_to=${config.websiteUrl}&")
                            append("lang=${config.languageCode}")
                        },
                    ).addHeader("Accept", "text/html")
                    .apply {
                        val cookies = getTelegramCookies()
                        if (cookies != null) addHeader("Cookie", cookies)
                    }.build()

            client.newCall(request).execute().use { response ->
                response.body.string()
            }
        } catch (_: Exception) {
            null
        }
    }

actual suspend fun loadImage(url: String): ByteArray? =
    withContext(Dispatchers.IO) {
        try {
            val client = getClient()
            val request =
                Request
                    .Builder()
                    .url(url)
                    .build()
            client.newCall(request).execute().use { response ->
                response.body.bytes()
            }
        } catch (_: Exception) {
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
