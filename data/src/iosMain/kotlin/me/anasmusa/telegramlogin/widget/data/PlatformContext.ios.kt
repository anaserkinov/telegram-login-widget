package me.anasmusa.telegramlogin.widget.data

import me.anasmusa.telegramlogin.widget.data.PlatformContext

actual abstract class PlatformContext {
    companion object {
        val INSTANCE = object : PlatformContext() {}
    }
}
