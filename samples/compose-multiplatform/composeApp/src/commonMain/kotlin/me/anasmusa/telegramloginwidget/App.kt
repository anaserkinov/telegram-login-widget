package me.anasmusa.telegramloginwidget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.anasmusa.shared.TelegramLoginManager
import me.anasmusa.shared.TelegramLoginResult
import me.anasmusa.telegramloginwidget.icons.LightModeIcon
import me.anasmusa.telegramloginwidget.icons.NightModeIcon

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var nightModeEnabled by remember { mutableStateOf(true) }
    var user by remember { mutableStateOf<TelegramLoginResult.Success?>(null) }

    val colorScheme =
        if (nightModeEnabled) {
            darkColorScheme(primaryContainer = Color(0xFF0E121F))
        } else {
            lightColorScheme(primaryContainer = Color.White)
        }
    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
        ) {
            Box(
                modifier =
                    Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(it)
                        .fillMaxSize()
                        .padding(top = 16.dp)
                        .padding(horizontal = 24.dp),
            ) {
                IconButton(
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd),
                    onClick = {
                        nightModeEnabled = !nightModeEnabled
                    },
                ) {
                    Icon(
                        imageVector =
                            if (nightModeEnabled) {
                                LightModeIcon
                            } else {
                                NightModeIcon
                            },
                        contentDescription = null,
                    )
                }

                if (user != null) {
                    MainScreen(
                        user = user!!,
                        backToLoginScreen = {
                            user = null
                        },
                        onLogout = {
                            scope.launch {
                                TelegramLoginManager.logout()
                                user = null
                            }
                        },
                    )
                } else {
                    LoginScreen(
                        onResult = {
                            when (it) {
                                is TelegramLoginResult.Success -> {
                                    user = it
                                }
                                TelegramLoginResult.Cancelled -> {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Canceled")
                                    }
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}
