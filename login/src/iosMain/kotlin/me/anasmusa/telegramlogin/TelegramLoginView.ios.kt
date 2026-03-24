package me.anasmusa.telegramlogin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCSignatureOverride
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDataBase64DecodingIgnoreUnknownCharacters
import platform.Foundation.NSError
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.UIKit.UIApplication
import platform.UIKit.UIScreen
import platform.UIKit.UIUserInterfaceStyle
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebsiteDataStore
import platform.WebKit.javaScriptEnabled
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun TelegramLoginView(
    config: TelegramLoginConfig,
    modifier: Modifier,
    pageLoader: @Composable BoxScope.() -> Unit,
    onResult: (TelegramLoginResult) -> Unit,
) {
    var canGoBack by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val telegramUrl = remember(config) { config.buildTelegramAuthUrl() }

    val coordinator =
        remember(config) {
            TelegramWebViewCoordinator(
                redirectURI = config.redirectURI,
                onResult = onResult,
                onLoadingChanged = { isLoading = it },
            )
        }

    val webView =
        remember(coordinator) {
            val configuration =
                WKWebViewConfiguration().apply {
                    preferences.javaScriptEnabled = true
                    websiteDataStore = WKWebsiteDataStore.defaultDataStore()
                    WKWebsiteDataStore.defaultDataStore().httpCookieStore
                }

            WKWebView(frame = UIScreen.mainScreen.bounds, configuration = configuration).apply {
                this.canGoBack
                navigationDelegate = coordinator
                if (config.uiMode == UiMode.Dark) {
                    overrideUserInterfaceStyle = UIUserInterfaceStyle.UIUserInterfaceStyleDark
                } else if (config.uiMode == UiMode.Dark) {
                    overrideUserInterfaceStyle = UIUserInterfaceStyle.UIUserInterfaceStyleLight
                }
                val request = NSURLRequest.requestWithURL(NSURL.URLWithString(telegramUrl)!!)
                loadRequest(request)
            }
        }

    LaunchedEffect(isLoading) {
        if (!isLoading) {
            canGoBack = webView.canGoBack
        }
    }

    Box(modifier = modifier) {
        UIKitView(
            factory = { webView },
            modifier = Modifier.fillMaxSize(),
        )

        if (canGoBack) {
            IconButton(
                onClick = {
                    webView.goBack()
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

@OptIn(ExperimentalForeignApi::class)
class TelegramWebViewCoordinator(
    private val redirectURI: String,
    private val onResult: (TelegramLoginResult) -> Unit,
    private val onLoadingChanged: (Boolean) -> Unit,
) : NSObject(),
    WKNavigationDelegateProtocol {
    @ObjCSignatureOverride
    override fun webView(
        webView: WKWebView,
        didStartProvisionalNavigation: WKNavigation?,
    ) {
        onLoadingChanged(true)
    }

    @ObjCSignatureOverride
    override fun webView(
        webView: WKWebView,
        didFinishNavigation: WKNavigation?,
    ) {
        onLoadingChanged(false)
    }

    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit,
    ) {
        val url = decidePolicyForNavigationAction.request.URL

        val urlString = url?.absoluteString ?: ""
        when {
            urlString.contains("tg://resolve") || urlString.contains("tg:resolve") -> {
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
                UIApplication.sharedApplication.openURL(url!!, options = emptyMap<Any?, Any?>(), completionHandler = { success ->
                    if (!success) {
                        UIApplication.sharedApplication.openURL(
                            NSURL.URLWithString("itms-apps://itunes.apple.com/app/id686449807")!!,
                            options = emptyMap<Any?, Any?>(),
                            completionHandler = null,
                        )
                    }
                })
            }

            urlString.startsWith(redirectURI) -> {
                val fragment = url!!.fragment
                val authData = fragment?.removePrefix("tgAuthResult=")
                onResult.invoke(parse(authData))
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
            }

            else -> {
                decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
            }
        }
    }

    @ObjCSignatureOverride
    override fun webView(
        webView: WKWebView,
        didFailProvisionalNavigation: WKNavigation?,
        withError: NSError,
    ) {
        onLoadingChanged(false)
    }
}

@OptIn(BetaInteropApi::class)
private fun NSData.convertToString() = NSString.create(data = this, encoding = NSUTF8StringEncoding)?.toString()

@OptIn(BetaInteropApi::class, ExperimentalForeignApi::class)
private fun decodeJwtPayload(token: String): Map<*, *>? {
    return try {
        val parts = token.split(".")
        if (parts.size != 3) return null

        var payload =
            parts[1]
                .replace('-', '+')
                .replace('_', '/')

        val pad = payload.length % 4
        if (pad != 0) payload += "=".repeat(4 - pad)

        NSData
            .create(
                base64EncodedString = payload,
                options = NSDataBase64DecodingIgnoreUnknownCharacters,
            )?.convertToString()
            ?.encodeToByteArray()
            ?.toNSData()
            ?.let { nsData ->
                NSJSONSerialization.JSONObjectWithData(
                    nsData,
                    options = 0u,
                    error = null,
                ) as? Map<*, *>
            }
    } catch (_: Exception) {
        null
    }
}

@OptIn(BetaInteropApi::class)
private fun parse(authData: String?): TelegramLoginResult {
    if (authData == null) return TelegramLoginResult.Cancelled
    var authData =
        authData
            .replace('-', '+')
            .replace('_', '/')
    val pad = authData.length % 4
    if (pad != 4) authData += "=".repeat(4 - pad)

    val idToken =
        NSData
            .create(
                base64EncodedString = authData,
                options = NSDataBase64DecodingIgnoreUnknownCharacters,
            )?.convertToString() ?: return TelegramLoginResult.Cancelled
    val json = decodeJwtPayload(idToken) ?: return TelegramLoginResult.Cancelled

    println("json: $json")
    json.forEach {
        println("${it.key}: ${it.value}")
    }

    return try {
        TelegramLoginResult.Success(
            idToken = idToken,
            user =
                TelegramUserData(
                    iss = json["iss"] as String,
                    aud = json["aud"] as String,
                    sub = json["sub"] as String,
                    iat = json["iat"] as Long,
                    exp = json["exp"] as Long,
                    id = (json["id"] as String).toLong(),
                    name = json["name"] as String,
                    preferredUsername = json["preferred_username"] as? String,
                    picture = json["picture"] as? String,
                    phoneNumber = json["phone_number"] as? String,
                    nonce = json["nonce"] as? String,
                ),
        )
    } catch (_: Exception) {
        TelegramLoginResult.Cancelled
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData.dataWithBytes(null, 0u) // Handle empty array case

    return this.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
    }
}
