package me.anasmusa.telegramlogin

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
expect fun TelegramLoginView(
    config: TelegramLoginConfig,
    modifier: Modifier = Modifier,
    pageLoader: @Composable BoxScope.() -> Unit = {
        CircularProgressIndicator(
            modifier =
                Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter),
            strokeWidth = 3.dp,
            color = TelegramDefaults.primaryColor,
        )
    },
    onResult: (TelegramLoginResult) -> Unit,
)
