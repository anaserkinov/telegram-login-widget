package me.anasmusa.shared

internal expect suspend fun getButtonHtml(config: TelegramLoginConfig): String?

internal expect suspend fun loadImage(url: String): ByteArray?

internal expect suspend fun getTelegramCookies(): String?

internal expect suspend fun clearTelegramCookies()
