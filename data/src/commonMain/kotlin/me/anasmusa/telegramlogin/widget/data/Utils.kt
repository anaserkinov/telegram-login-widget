package me.anasmusa.telegramlogin.widget.data

fun TelegramLoginConfig.buildTelegramAuthUrl() =
    buildString {
        append("https://oauth.telegram.org/auth?")
        append("bot_id=$botId")
        append("&origin=$websiteUrl")
        append("&lang=$languageCode")
        if (requestDirectMessages) {
            append("&request_access=write")
        }
    }
