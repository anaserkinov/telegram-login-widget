package me.anasmusa.androidnative

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.anasmusa.telegramlogin.TelegramButtonIcon
import me.anasmusa.telegramlogin.TelegramDefaults
import me.anasmusa.telegramlogin.TelegramLoginButton
import me.anasmusa.telegramlogin.TelegramLoginConfig
import me.anasmusa.telegramlogin.TelegramLoginOutlinedButton
import me.anasmusa.telegramlogin.TelegramLoginResult
import me.anasmusa.telegramlogin.UiMode

@Preview
@Composable
private fun LoginsScreenPreview() {
    LoginScreen(
        onResult = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onResult: (result: TelegramLoginResult) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 48.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val config =
            TelegramLoginConfig(
                clientId = 8266153417L,
                redirectURI = "https://anasmusa.me",
                requestPhoneNumber = true,
                nonce = "secret_string",
                uiMode =
                    if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES || isSystemInDarkTheme()) {
                        UiMode.Dark
                    } else {
                        UiMode.Light
                    },
            )

        TelegramLoginButton(
            config = config,
            modifier =
                Modifier
                    .fillMaxWidth(),
            onResult = onResult,
        ) {
            Text(text = "Sign in with Telegram")
        }

        TelegramLoginButton(
            config = config,
            modifier =
                Modifier
                    .fillMaxWidth(),
            onResult = onResult,
            icon = {
                TelegramButtonIcon(tint = TelegramDefaults.primaryColor)
            },
            colors =
                TelegramDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
        ) {
            Text(text = "Sign in with Telegram")
        }

        TelegramLoginButton(
            config = config,
            modifier =
                Modifier
                    .fillMaxWidth(),
            onResult = onResult,
        ) {
            Text(
                text = "Sign in with Telegram",
                modifier =
                    Modifier
                        .padding(end = TelegramDefaults.iconSize)
                        .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        TelegramLoginOutlinedButton(
            config = config,
            modifier =
                Modifier
                    .fillMaxWidth(),
            onResult = onResult,
        ) {
            Text(text = "Sign in with Telegram")
        }

        TelegramLoginOutlinedButton(
            config = config,
            modifier =
                Modifier
                    .fillMaxWidth(),
            onResult = onResult,
        ) {
            Text(
                text = "Sign in with Telegram",
                modifier =
                    Modifier
                        .padding(end = TelegramDefaults.iconSize)
                        .fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}
