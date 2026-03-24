package me.anasmusa.telegramlogin

sealed interface TelegramLoginResult {
    data class Success(
        val idToken: String,
        val user: TelegramUserData,
    ) : TelegramLoginResult

    data object Cancelled : TelegramLoginResult
}

data class TelegramUserData(
    val iss: String,
    val aud: String,
    val sub: String,
    val iat: Long,
    val exp: Long,
    val id: Long,
    val name: String,
    val preferredUsername: String?,
    val picture: String?,
    val phoneNumber: String?,
    val nonce: String?,
)
