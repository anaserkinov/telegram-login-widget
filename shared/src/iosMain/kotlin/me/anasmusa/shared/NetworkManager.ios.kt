package me.anasmusa.shared

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSDate
import platform.Foundation.NSHTTPCookie
import platform.Foundation.NSHTTPURLResponse
import platform.Foundation.NSMutableURLRequest
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataTaskWithRequest
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.distantPast
import platform.Foundation.setHTTPMethod
import platform.Foundation.setValue
import platform.WebKit.WKWebsiteDataStore
import platform.WebKit.WKWebsiteDataTypeCookies
import platform.posix.memcpy
import kotlin.coroutines.resume

@OptIn(BetaInteropApi::class)
actual suspend fun getButtonHtml(config: TelegramLoginConfig): String? =
    withContext(Dispatchers.IO) {
        val cookies = getTelegramCookies()
        suspendCancellableCoroutine { continuation ->
            try {
                val urlString =
                    buildString {
                        append("https://oauth.telegram.org/embed/${config.botUsername}?")
                        append("origin=${config.websiteUrl}&")
                        append("return_to=${config.websiteUrl}&")
                        append("lang=${config.languageCode}")
                    }

                val url =
                    NSURL.URLWithString(urlString) ?: run {
                        continuation.resume(null)
                        return@suspendCancellableCoroutine
                    }

                val request =
                    NSMutableURLRequest(uRL = url).apply {
                        setHTTPMethod("GET")
                        setValue("text/html", forHTTPHeaderField = "Accept")

                        if (cookies != null) {
                            setValue(cookies, forHTTPHeaderField = "Cookie")
                        }
                    }

                val task =
                    NSURLSession.sharedSession.dataTaskWithRequest(request) { data, response, _ ->
                        val httpResponse = response as? NSHTTPURLResponse
                        val statusCode = httpResponse?.statusCode?.toInt() ?: 0

                        if (statusCode in 200..299 && data != null) {
                            val html = NSString.create(data = data, encoding = NSUTF8StringEncoding)?.toString()
                            continuation.resume(html)
                        } else {
                            continuation.resume(null)
                        }
                    }

                continuation.invokeOnCancellation { task.cancel() }
                task.resume()
            } catch (_: Exception) {
            }
        }
    }

@OptIn(ExperimentalForeignApi::class)
actual suspend fun loadImage(url: String): ByteArray? =
    withContext(Dispatchers.IO) {
        try {
            val nsUrl =
                NSURL.URLWithString(url)
                    ?: error("Invalid URL")

            val data =
                NSData.dataWithContentsOfURL(nsUrl)
                    ?: error("Failed to download image")

            ByteArray(data.length().toInt()).apply {
                usePinned { pinned ->
                    memcpy(pinned.addressOf(0), data.bytes, data.length())
                }
            }
        } catch (_: Exception) {
            null
        }
    }

actual suspend fun getTelegramCookies(): String? =
    withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            WKWebsiteDataStore.defaultDataStore().httpCookieStore.getAllCookies { cookies ->
                val result =
                    cookies
                        ?.filterIsInstance<NSHTTPCookie>()
                        ?.filter { it.domain.contains("telegram.org") }
                        ?.joinToString("; ") { "${it.name}=${it.value}" }
                        ?.takeIf { it.isNotEmpty() }
                continuation.resume(result)
            }
        }
    }

actual suspend fun clearTelegramCookies(): Unit =
    withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val store = WKWebsiteDataStore.defaultDataStore()
            val cookieNames = listOf("stel_ssid", "stel_acid", "stel_token")

            // Snapshot cookies to keep BEFORE wiping
            store.httpCookieStore.getAllCookies { cookies ->
                val toKeep =
                    cookies
                        ?.filterIsInstance<NSHTTPCookie>()
                        ?.filter { it.name !in cookieNames }

                // Full flush
                store.removeDataOfTypes(
                    dataTypes = setOf(WKWebsiteDataTypeCookies),
                    modifiedSince = NSDate.distantPast,
                ) {
                    if (toKeep.isNullOrEmpty()) {
                        continuation.resume(Unit)
                        return@removeDataOfTypes
                    }
                    // Re-add kept cookies
                    var remaining = toKeep.size
                    toKeep.forEach { cookie ->
                        store.httpCookieStore.setCookie(cookie) {
                            if (--remaining == 0) continuation.resume(Unit)
                        }
                    }
                }
            }
        }
    }
