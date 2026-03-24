package me.anasmusa.telegramloginwidget

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import me.anasmusa.telegramlogin.UiMode

var localNightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

@Composable
actual fun getUiMode(): UiMode =
    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || isSystemInDarkTheme()) {
        UiMode.Dark
    } else {
        UiMode.Light
    }

actual fun setUiMode(uiMode: UiMode) {
    localNightMode =
        when (uiMode) {
            UiMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
            UiMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
            UiMode.Unspecified -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

    AppCompatDelegate.setDefaultNightMode(localNightMode)
}
