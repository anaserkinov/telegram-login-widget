package me.anasmusa.telegramloginwidget

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import me.anasmusa.shared.PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = LocalContext.current
