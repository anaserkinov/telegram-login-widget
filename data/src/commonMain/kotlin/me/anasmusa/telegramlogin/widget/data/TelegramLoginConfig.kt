package me.anasmusa.telegramlogin.widget.data

data class TelegramLoginConfig(
    val botId: Long,
    val botUsername: String,
    val websiteUrl: String,
    val requestDirectMessages: Boolean = true,
    val languageCode: String = "en",
)
