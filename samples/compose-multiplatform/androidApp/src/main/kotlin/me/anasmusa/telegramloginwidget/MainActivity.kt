package me.anasmusa.telegramloginwidget

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(localNightMode)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            val view = LocalView.current
            val nightModeEnabled = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || isSystemInDarkTheme()

            SideEffect {
                window.let { window ->
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !nightModeEnabled
                }
            }

            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
