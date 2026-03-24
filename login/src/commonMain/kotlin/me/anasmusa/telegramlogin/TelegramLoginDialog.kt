package me.anasmusa.telegramlogin

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelegramLoginDialog(
    config: TelegramLoginConfig,
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
    properties: DialogProperties = DialogProperties(),
    onResult: (TelegramLoginResult) -> Unit,
) {
    Dialog(
        onDismissRequest = {
            onResult.invoke(TelegramLoginResult.Cancelled)
        },
        properties = properties,
    ) {
        TelegramLoginView(
            config = config,
            modifier =
                Modifier
                    .heightIn(min = 620.dp)
                    .fillMaxHeight(fraction = 0.6f)
                    .widthIn(max = 530.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
            pageLoader = pageLoader,
            onResult = onResult,
        )
    }
}
