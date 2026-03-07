package me.anasmusa.telegramloginwidget

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.anasmusa.shared.TelegramLoginConfig
import me.anasmusa.shared.TelegramLoginResult

@Composable
expect fun TelegramLoginView(
    config: TelegramLoginConfig,
    modifier: Modifier = Modifier,
    pageLoader: @Composable BoxScope.() -> Unit = {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .align(Alignment.TopCenter),
            strokeWidth = 3.dp,
        )
    },
    onResult: (TelegramLoginResult) -> Unit,
)
