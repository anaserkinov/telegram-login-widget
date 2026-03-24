package me.anasmusa.androidnative

import androidx.activity.compose.LocalActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch
import me.anasmusa.androidnative.icons.LightModeIcon
import me.anasmusa.androidnative.icons.NightModeIcon
import me.anasmusa.telegramlogin.TelegramLoginResult
import me.anasmusa.telegramlogin.widget.data.TelegramLoginManager

@Composable
@Preview
fun App() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val nightModeEnabled =
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> isSystemInDarkTheme()
        }
    var useLegacyMethod by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<User?>(null) }

    val activity = LocalActivity.current
    val view = LocalView.current
    SideEffect {
        activity?.window?.let { window ->
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !nightModeEnabled
        }
    }

    MaterialTheme(
        colorScheme =
            if (nightModeEnabled) {
                darkColorScheme(primaryContainer = Color(0xFF0E121F))
            } else {
                lightColorScheme(primaryContainer = Color.White)
            },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (user == null) {
                        Button(
                            modifier =
                                Modifier
                                    .weight(1f),
                            onClick = { useLegacyMethod = !useLegacyMethod },
                        ) {
                            Text(
                                text =
                                    if (useLegacyMethod) {
                                        "Switch to telegram-login"
                                    } else {
                                        "Switch to telegram-widget"
                                    },
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = {
                            localNightMode =
                                if (nightModeEnabled) {
                                    AppCompatDelegate.MODE_NIGHT_NO
                                } else {
                                    AppCompatDelegate.MODE_NIGHT_YES
                                }
                            AppCompatDelegate.setDefaultNightMode(localNightMode)
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
                    if (useLegacyMethod) {
                        LoginScreenLegacy(
                            onResult = {
                                when (it) {
                                    is me.anasmusa.telegramlogin.widget.data.TelegramLoginResult.Success -> {
                                        user =
                                            User(
                                                photoUrl = it.photoUrl,
                                                fullName = it.firstName + if (it.lastName != null) " ${it.lastName}" else "",
                                                username = it.username,
                                                phoneNumber = null,
                                                loggedInWithWidget = true,
                                            )
                                    }

                                    me.anasmusa.telegramlogin.widget.data.TelegramLoginResult.Cancelled -> {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Canceled")
                                        }
                                    }
                                }
                            },
                        )
                    } else {
                        LoginScreen(
                            onResult = {
                                when (it) {
                                    is TelegramLoginResult.Success -> {
                                        user =
                                            User(
                                                photoUrl = it.user.picture,
                                                fullName = it.user.name,
                                                username = it.user.preferredUsername,
                                                phoneNumber = it.user.phoneNumber,
                                                loggedInWithWidget = false,
                                            )
                                    }

                                    is TelegramLoginResult.Cancelled -> {
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
}
