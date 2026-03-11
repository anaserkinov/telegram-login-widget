package me.anasmusa.androidnative

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.anasmusa.shared.TelegramLoginResult
import me.anasmusa.telegramloginwidget.TelegramButtonIcon
import me.anasmusa.telegramloginwidget.TelegramButtonText
import me.anasmusa.telegramloginwidget.TelegramButtonUserPhotoBox
import me.anasmusa.telegramloginwidget.TelegramDefaults
import me.anasmusa.telegramloginwidget.TelegramLoginBottomSheet
import me.anasmusa.telegramloginwidget.TelegramLoginButton
import me.anasmusa.telegramloginwidget.TelegramLoginOutlinedButton
import me.anasmusa.telegramloginwidget.rememberTelegramLoginState

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
    val state =
        rememberTelegramLoginState(
            botId = 8320475019L,
            botUsername = "login_widget_telegram_bot",
            websiteUrl = "https://anasmusa.me",
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TelegramLoginButton(
            state = state,
            onResult = onResult,
            modifier =
                Modifier
                    .fillMaxWidth(),
        )

        TelegramLoginButton(
            state = state,
            onResult = onResult,
            modifier =
                Modifier
                    .fillMaxWidth(),
            left = {
                TelegramButtonIcon(tint = TelegramDefaults.primaryColor)
            },
            colors =
                TelegramDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black,
                ),
        )

        TelegramLoginButton(
            state = state,
            onResult = onResult,
            modifier =
                Modifier
                    .fillMaxWidth(),
            center = {
                TelegramButtonText(
                    state = it,
                    modifier =
                        Modifier
                            .weight(1f),
                )
            },
        )

        TelegramLoginButton(
            state = state,
            onResult = onResult,
            modifier = Modifier.fillMaxWidth(),
            center = {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TelegramButtonText(
                        state = it,
                        modifier = Modifier.weight(weight = 1f, fill = false),
                    )
                    TelegramButtonUserPhotoBox(state = it)
                }
            },
            right = null,
        )

        TelegramLoginOutlinedButton(
            state = state,
            onResult = onResult,
            modifier =
                Modifier
                    .fillMaxWidth(),
        )

        TelegramLoginOutlinedButton(
            state = state,
            onResult = onResult,
            modifier =
                Modifier
                    .fillMaxWidth(),
            center = {
                TelegramButtonText(
                    state = it,
                    modifier =
                        Modifier
                            .weight(1f),
                )
            },
        )

        TelegramLoginOutlinedButton(
            state = state,
            onResult = onResult,
            modifier =
                Modifier
                    .fillMaxWidth(),
            center = {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TelegramButtonText(
                        state = it,
                        modifier = Modifier.weight(weight = 1f, fill = false),
                    )
                    TelegramButtonUserPhotoBox(state = it)
                }
            },
            right = null,
        )

        var showBottomSheet by remember { mutableStateOf(false) }

        FilledIconButton(
            onClick = {
                showBottomSheet = true
            },
            modifier =
                Modifier
                    .size(48.dp),
            colors =
                IconButtonDefaults.filledIconButtonColors(
                    containerColor = TelegramDefaults.primaryColor,
                ),
        ) {
            Icon(
                imageVector = TelegramDefaults.icon,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(24.dp),
                tint = Color.White,
            )
        }

        if (showBottomSheet) {
            TelegramLoginBottomSheet(
                config = state.config,
                modifier = Modifier,
            ) {
                showBottomSheet = false
                state.reload()
                onResult(it)
            }
        }
    }
}
