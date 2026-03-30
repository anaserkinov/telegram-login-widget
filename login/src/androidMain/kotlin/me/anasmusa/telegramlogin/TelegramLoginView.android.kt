package me.anasmusa.telegramlogin

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Base64
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import org.json.JSONObject

@Composable
actual fun TelegramLoginView(
    config: TelegramLoginConfig,
    modifier: Modifier,
    pageLoader: @Composable BoxScope.() -> Unit,
    onResult: (TelegramLoginResult) -> Unit,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    val telegramUrl =
        remember(config) {
            config.buildTelegramAuthUrl()
        }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            canGoBack = webView?.canGoBack() == true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            webView?.destroy()
        }
    }

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onResult.invoke(TelegramLoginResult.Cancelled)
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            modifier =
                Modifier
                    .fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webView = this
                    layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT)

                    settings.javaScriptEnabled = true

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (config.uiMode == UiMode.Dark) {
                            settings.forceDark = WebSettings.FORCE_DARK_ON
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

                                if (url != null && (url.toString().startsWith("tg:"))) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, url)
                                        context.startActivity(intent)
                                    } catch (_: ActivityNotFoundException) {
                                        val intent = Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=org.telegram.messenger".toUri())
                                        context.startActivity(intent)
                                    }
                                    return true
                                }

                                if (url != null && url.toString().startsWith(config.redirectURI)) {
                                    val hash = url.fragment // gives "tgAuthResult=eyJpZCI6..."
                                    val authData = hash?.removePrefix("tgAuthResult=")
                                    onResult.invoke(parse(authData))
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
                            }
                        }

                    loadUrl(telegramUrl)
                }
            },
            update = {
                it.loadUrl(telegramUrl)
            },
        )

        if (canGoBack) {
            IconButton(
                onClick = {
                    webView?.goBack()
                },
                modifier =
                    Modifier
                        .padding(10.dp),
            ) {
                Icon(
                    imageVector = ChevronLeftIcon,
                    contentDescription = null,
                )
            }
        }

        if (isLoading) pageLoader()
    }
}

private fun decodeJwtPayload(token: String): JSONObject? {
    return try {
        val parts = token.split(".")
        if (parts.size != 3) return null

        var payload =
            parts[1]
                .replace('-', '+')
                .replace('_', '/')

        val pad = payload.length % 4
        if (pad != 0) {
            payload += "=".repeat(4 - pad)
        }

        val decodedString =
            Base64
                .decode(payload, Base64.DEFAULT)
                .toString(Charsets.UTF_8)

        JSONObject(decodedString)
    } catch (_: Exception) {
        null
    }
}

private fun parse(authData: String?): TelegramLoginResult {
    if (authData == null) return TelegramLoginResult.Cancelled
    val idToken = Base64.decode(authData, Base64.URL_SAFE).toString(Charsets.UTF_8).removeSurrounding("\"")
    val json = decodeJwtPayload(idToken) ?: return TelegramLoginResult.Cancelled
    return try {
        TelegramLoginResult.Success(
            idToken = idToken,
            user =
                TelegramUserData(
                    iss = json.getString("iss"),
                    aud = json.getString("aud"),
                    sub = json.getString("sub"),
                    iat = json.getLong("iat"),
                    exp = json.getLong("exp"),
                    id =
                        json.optLong(
                            "id",
                            json.getString("id").toLong(),
                        ),
                    name = json.getString("name"),
                    preferredUsername = if (json.has("preferred_username")) json.getString("preferred_username") else null,
                    picture = if (json.has("picture")) json.getString("picture") else null,
                    phoneNumber = if (json.has("phone_number")) json.getString("phone_number") else null,
                    nonce = if (json.has("nonce")) json.getString("nonce") else null,
                ),
        )
    } catch (_: Exception) {
        TelegramLoginResult.Cancelled
    }
}
