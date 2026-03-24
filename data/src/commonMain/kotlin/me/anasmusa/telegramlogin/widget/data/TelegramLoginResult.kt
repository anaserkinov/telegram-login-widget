package me.anasmusa.telegramlogin.widget.data

sealed interface TelegramLoginResult {
    data class Success(
        val id: Long,
        val firstName: String,
        val lastName: String?,
        val username: String?,
        val photoUrl: String?,
        val authDate: Long,
        val hash: String,
    ) : TelegramLoginResult

    data object Cancelled : TelegramLoginResult
}
