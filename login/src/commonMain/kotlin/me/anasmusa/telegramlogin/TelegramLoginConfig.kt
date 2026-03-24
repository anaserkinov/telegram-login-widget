package me.anasmusa.telegramlogin

import androidx.compose.runtime.Immutable

@Immutable
data class TelegramLoginConfig(
    val clientId: Long,
    val redirectURI: String,
    val requestDirectMessages: Boolean = true,
    val requestPhoneNumber: Boolean = false,
    val nonce: String? = null,
    val languageCode: String? = null,
    val uiMode: UiMode = UiMode.Unspecified,
) {
    fun buildTelegramAuthUrl() =
        buildString {
            append(
                "https://oauth.telegram.org/auth?response_type=post_message",
            )
            append("&client_id=").append(clientId)
            append("&redirect_uri=").append(redirectURI)
            append("&scope=")
                .append("openid profile")
            if (requestPhoneNumber) {
                append(" phone")
            }
            if (requestDirectMessages) {
                append(" telegram:bot_access")
            }
            if (nonce != null) {
                append("&nonce=").append(nonce)
            }
            if (languageCode != null) {
                append("&lang=").append(languageCode)
            }
        }
}
