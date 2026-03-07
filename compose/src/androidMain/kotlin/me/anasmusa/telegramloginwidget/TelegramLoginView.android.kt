package me.anasmusa.telegramloginwidget

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import me.anasmusa.shared.TelegramLoginConfig
import me.anasmusa.shared.TelegramLoginResult
import org.json.JSONObject

@Composable
actual fun TelegramLoginView(
    config: TelegramLoginConfig,
    modifier: Modifier,
    pageLoader: @Composable BoxScope.() -> Unit,
    onResult: (TelegramLoginResult) -> Unit,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }

    var isLoading by remember { mutableStateOf(true) }

    val telegramUrl =
        remember(config) {
            buildString {
                append("https://oauth.telegram.org/auth?")
                append("bot_id=${config.botId}")
                append("&origin=${config.websiteUrl}")
                append("&lang=${config.languageCode}")
                if (config.requestAccess) {
                    append("&request_access=write")
                }
            }
        }

    DisposableEffect(Unit) {
        onDispose {
            CookieManager.getInstance().flush()
            webView?.destroy()
        }
    }

    Box(modifier = modifier.heightIn(min = 600.dp)) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webView = this
                    setBackgroundColor(Color.WHITE)
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true

                    addJavascriptInterface(TelegramJsInterface(onResult), "AndroidTelegramHandler")

                    var pushRequested = false
                    var authData: String? = null

                    fun check() {
                        if (pushRequested && !authData.isNullOrBlank()) {
                            onResult.invoke(
                                parseJson(
                                    Base64.decode(authData, Base64.DEFAULT).toString(Charsets.UTF_8),
                                ),
                            )
                        }
                    }

                    webViewClient =
                        object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView,
                                url: String?,
                                favicon: Bitmap?,
                            ) {
                                super.onPageStarted(view, url, favicon)
                                isLoading = true
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?,
                            ): Boolean {
                                val url = request?.url
                                if (url != null && url.toString().startsWith(config.websiteUrl)) {
                                    val hash = url.fragment // gives "tgAuthResult=eyJpZCI6..."
                                    authData = hash?.removePrefix("tgAuthResult=")
                                    check()
                                    return true
                                }
                                return super.shouldOverrideUrlLoading(view, request)
                            }

                            override fun onPageFinished(
                                view: WebView,
                                url: String?,
                            ) {
                                super.onPageFinished(view, url)
                                isLoading = false
                                view.evaluateJavascript(buildCancelOverrideJs(), null)
                                if (url?.startsWith("https://oauth.telegram.org/auth/push") == true) {
                                    pushRequested = true
                                    check()
                                }
                            }
                        }

                    loadUrl(telegramUrl)
                }
            },
            update = {
                it.loadUrl(telegramUrl)
            },
        )

        if (isLoading) pageLoader()
    }
}

private fun buildCancelOverrideJs() =
    """
    (function() {
        // Override window.close to intercept Telegram's close attempts
        var _originalClose = window.close.bind(window);
        window.close = function() {
            AndroidTelegramHandler.onCancel();
            // Optionally call original: _originalClose();
        };
    })();
    """.trimIndent()

// ── JS Bridge ─────────────────────────────────────────────────────────────────

class TelegramJsInterface(
    private val onResult: (TelegramLoginResult) -> Unit,
) {
    @JavascriptInterface
    fun onCancel() {
        Handler(Looper.getMainLooper()).post { onResult(TelegramLoginResult.Cancelled) }
    }
}

private fun parseJson(jsonString: String): TelegramLoginResult.Success {
    val json = JSONObject(jsonString)
    return TelegramLoginResult.Success(
        id = json.getLong("id"),
        firstName = json.getString("first_name"),
        lastName = if (json.has("last_name")) json.getString("last_name") else null,
        username = if (json.has("username")) json.getString("username") else null,
        photoUrl = if (json.has("photo_url")) json.getString("photo_url") else null,
        authDate = json.getLong("auth_date"),
        hash = json.getString("hash"),
    )
}
