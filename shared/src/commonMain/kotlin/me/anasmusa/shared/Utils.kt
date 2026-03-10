package me.anasmusa.shared

fun TelegramLoginConfig.buildTelegramAuthUrl() =
    buildString {
        append("https://oauth.telegram.org/auth?")
        append("bot_id=$botId")
        append("&origin=$websiteUrl")
        append("&lang=$languageCode")
        if (requestAccess) {
            append("&request_access=write")
        }
    }
