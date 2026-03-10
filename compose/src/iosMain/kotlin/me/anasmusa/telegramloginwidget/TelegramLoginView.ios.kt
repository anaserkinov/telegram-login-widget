package me.anasmusa.telegramloginwidget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
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
import me.anasmusa.shared.TelegramLoginConfig
import me.anasmusa.shared.TelegramLoginResult
import me.anasmusa.shared.buildTelegramAuthUrl
import platform.Foundation.NSData
import platform.Foundation.NSDataBase64DecodingIgnoreUnknownCharacters
import platform.Foundation.NSError
import platform.Foundation.NSJSONSerialization
import platform.Foundation.NSNumber
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.UIKit.UIColor
import platform.UIKit.UIScreen
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationAction
import platform.WebKit.WKNavigationActionPolicy
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKScriptMessage
import platform.WebKit.WKScriptMessageHandlerProtocol
import platform.WebKit.WKUserContentController
import platform.WebKit.WKWebView
import platform.WebKit.WKWebViewConfiguration
import platform.WebKit.WKWebsiteDataStore
import platform.WebKit.javaScriptEnabled
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun TelegramLoginView(
    config: TelegramLoginConfig,
    modifier: Modifier,
    pageLoader: @Composable BoxScope.() -> Unit,
    onResult: (TelegramLoginResult) -> Unit,
) {
    var isLoading by remember { mutableStateOf(true) }

    val telegramUrl = remember(config) { config.buildTelegramAuthUrl() }

    val coordinator =
        remember(config) {
            TelegramWebViewCoordinator(
                websiteUrl = config.websiteUrl,
                onResult = onResult,
                onLoadingChanged = { isLoading = it },
            )
        }

    val webView =
        remember(coordinator) {
            val configuration =
                WKWebViewConfiguration().apply {
                    preferences.javaScriptEnabled = true
                    // Enable localStorage / sessionStorage
                    websiteDataStore = WKWebsiteDataStore.defaultDataStore()
                    WKWebsiteDataStore.defaultDataStore().httpCookieStore
                }

            WKWebView(frame = UIScreen.mainScreen.bounds, configuration = configuration).apply {
                navigationDelegate = coordinator
                backgroundColor = UIColor.whiteColor
                configuration.userContentController.addScriptMessageHandler(
                    coordinator,
                    name = "iOSTelegramHandler",
                )
                val request = NSURLRequest.requestWithURL(NSURL.URLWithString(telegramUrl)!!)
                loadRequest(request)
            }
        }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 600.dp),
    ) {
        // Embed WKWebView into Compose via UIKitView
        UIKitView(
            factory = { webView },
            modifier = Modifier.matchParentSize(),
        )

        if (isLoading) pageLoader()
    }
}

// ── Coordinator (Navigation Delegate + Script Message Handler) ────────────────

@OptIn(ExperimentalForeignApi::class)
class TelegramWebViewCoordinator(
    private val websiteUrl: String,
    private val onResult: (TelegramLoginResult) -> Unit,
    private val onLoadingChanged: (Boolean) -> Unit,
) : NSObject(),
    WKNavigationDelegateProtocol,
    WKScriptMessageHandlerProtocol {
    private var pushRequested = false
    private var authData: String? = null

    @OptIn(BetaInteropApi::class)
    private fun check() {
        if (pushRequested && !authData.isNullOrBlank()) {
            val standardBase64 =
                authData!!
                    .replace('-', '+')
                    .replace('_', '/')
            val padded =
                standardBase64.padEnd(
                    standardBase64.length + (4 - standardBase64.length % 4) % 4,
                    '=',
                )
            val decoded =
                NSData.create(
                    base64EncodedString = padded,
                    options = NSDataBase64DecodingIgnoreUnknownCharacters,
                )
            val json =
                decoded?.let {
                    NSString.create(data = it, encoding = NSUTF8StringEncoding)?.toString()
                }
            if (json != null) onResult(parseJson(json))
        }
    }

    // WKNavigationDelegate

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
        webView.evaluateJavaScript(buildCancelOverrideJs(), completionHandler = null)

        val currentUrl = webView.URL?.absoluteString ?: return
        if (currentUrl.startsWith("https://oauth.telegram.org/auth/push")) {
            pushRequested = true
            check()
        }
    }

    override fun webView(
        webView: WKWebView,
        decidePolicyForNavigationAction: WKNavigationAction,
        decisionHandler: (WKNavigationActionPolicy) -> Unit,
    ) {
        val url = decidePolicyForNavigationAction.request.URL

        val urlString = url?.absoluteString ?: ""
        if (urlString.startsWith(websiteUrl)) {
            // Fragment: tgAuthResult=eyJ...
            val fragment = url!!.fragment
            authData = fragment?.removePrefix("tgAuthResult=")
            check()
            decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyCancel)
            return
        }

        decisionHandler(WKNavigationActionPolicy.WKNavigationActionPolicyAllow)
    }

    @ObjCSignatureOverride
    override fun webView(
        webView: WKWebView,
        didFailProvisionalNavigation: WKNavigation?,
        withError: NSError,
    ) {
        onLoadingChanged(false)
    }

    // WKScriptMessageHandler
    override fun userContentController(
        userContentController: WKUserContentController,
        didReceiveScriptMessage: WKScriptMessage,
    ) {
        if (didReceiveScriptMessage.name == "iOSTelegramHandler") {
            val body = didReceiveScriptMessage.body as? String
            if (body == "cancel") {
                dispatch_async(dispatch_get_main_queue()) {
                    onResult(TelegramLoginResult.Cancelled)
                }
            }
        }
    }
}

// ── JS Injection ──────────────────────────────────────────────────────────────

private fun buildCancelOverrideJs() =
    """
    (function() {
        // Override window.close to intercept Telegram's close attempts
        var _originalClose = window.close.bind(window);
        window.close = function() {
            window.webkit.messageHandlers.iOSTelegramHandler.postMessage('cancel');
            // Optionally call original: _originalClose();
        };
    })();
    """.trimIndent()

// ── JSON Parsing ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalForeignApi::class)
private fun parseJson(jsonString: String): TelegramLoginResult.Success {
    val data = jsonString.encodeToByteArray()
    val nsData = data.toNSData()
    val json =
        NSJSONSerialization.JSONObjectWithData(
            nsData,
            options = 0u,
            error = null,
        ) as? Map<*, *> ?: return TelegramLoginResult.Success(
            id = 0,
            firstName = "",
            lastName = null,
            username = null,
            photoUrl = null,
            authDate = 0,
            hash = "",
        )

    return TelegramLoginResult.Success(
        id = (json["id"] as? NSNumber)?.longLongValue ?: 0L,
        firstName = json["first_name"] as? String ?: "",
        lastName = json["last_name"] as? String,
        username = json["username"] as? String,
        photoUrl = json["photo_url"] as? String,
        authDate = (json["auth_date"] as? NSNumber)?.longLongValue ?: 0L,
        hash = json["hash"] as? String ?: "",
    )
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData.dataWithBytes(null, 0u) // Handle empty array case

    return this.usePinned { pinned ->
        NSData.dataWithBytes(pinned.addressOf(0), size.toULong())
    }
}
