package me.anasmusa.telegramloginwidget

import androidx.compose.runtime.Composable
import me.anasmusa.telegramlogin.UiMode

@Composable
expect fun getUiMode(): UiMode

expect fun setUiMode(uiMode: UiMode)
