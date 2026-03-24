package me.anasmusa.telegramlogin.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import me.anasmusa.telegramlogin.widget.data.PlatformContext

@Composable
actual fun getPlatformContext(): PlatformContext = LocalContext.current
