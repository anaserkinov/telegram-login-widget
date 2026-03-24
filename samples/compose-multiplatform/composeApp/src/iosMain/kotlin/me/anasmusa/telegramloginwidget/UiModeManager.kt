package me.anasmusa.telegramloginwidget

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.anasmusa.telegramlogin.UiMode

private var localNightMode by mutableStateOf(UiMode.Unspecified)

@Composable
actual fun getUiMode(): UiMode =
    if (localNightMode == UiMode.Unspecified) {
        if (isSystemInDarkTheme()) UiMode.Dark else UiMode.Light
    } else {
        localNightMode
    }

actual fun setUiMode(uiMode: UiMode) {
    localNightMode = uiMode
}
