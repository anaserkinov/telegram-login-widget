package me.anasmusa.telegramloginwidget

import androidx.compose.runtime.Composable
import me.anasmusa.shared.PlatformContext

@Composable
expect fun getPlatformContext(): PlatformContext
