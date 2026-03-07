package me.anasmusa.shared

data class TelegramLoginConfig(
    val botId: Long,
    val botUsername: String,
    val websiteUrl: String,
    val requestAccess: Boolean = true,
    val languageCode: String = "en",
)
