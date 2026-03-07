package me.anasmusa.telegramloginwidget

import androidx.compose.runtime.Composable
import me.anasmusa.shared.PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = PlatformContext.INSTANCE
