package me.anasmusa.telegramlogin.widget

import androidx.compose.runtime.Composable
import me.anasmusa.telegramlogin.widget.data.PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = PlatformContext.INSTANCE
