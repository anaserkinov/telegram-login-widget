package me.anasmusa.telegramloginwidget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.anasmusa.shared.DataLoadState
import me.anasmusa.shared.PlatformContext
import me.anasmusa.shared.TelegramLoginConfig
import me.anasmusa.shared.TelegramLoginManager

@Composable
fun rememberTelegramLoginState(
    botId: Long,
    botUsername: String,
    websiteUrl: String,
    requestAccess: Boolean = true,
    languageCode: String = "en",
): TelegramLoginState {
    val coroutineScope = rememberCoroutineScope()
    val context = getPlatformContext()
    return remember(requestAccess, languageCode) {
        TelegramLoginState(
            context = context,
            config =
                TelegramLoginConfig(
                    botId = botId,
                    botUsername = botUsername,
                    websiteUrl = websiteUrl,
                    requestAccess = requestAccess,
                    languageCode = languageCode,
                ),
            scope = coroutineScope,
        )
    }
}

@Stable
class TelegramLoginState(
    private val context: PlatformContext,
    val config: TelegramLoginConfig,
    private val scope: CoroutineScope,
) {
    class ButtonContent(
        val text: String = "",
        val userFirstName: String? = null,
        val userPhoto: Painter? = null,
    )

    var isLoading by mutableStateOf(false)
        private set

    var buttonContent by mutableStateOf(ButtonContent())

    private var lastUsedCookies: String? = null

    init {
        load()
    }

    private fun load() {
        scope.launch {
            isLoading = true
            lastUsedCookies = TelegramLoginManager.getCookies()
            TelegramLoginManager.getButtonContent(context, config).collect {
                buttonContent =
                    ButtonContent(
                        text = it.text ?: "",
                        userFirstName = it.userFirstName,
                        userPhoto = it.userPhotoData?.let { BitmapPainter(it.toImageBitmap()) },
                    )
                isLoading = it.dataLoadState == DataLoadState.IN_PROGRESS
            }
        }
    }

    fun reload() {
        scope.launch {
            val cookies = TelegramLoginManager.getCookies()
            if (lastUsedCookies != cookies) {
                lastUsedCookies = cookies
                load()
            }
        }
    }

    fun logout() {
        scope.launch {
            TelegramLoginManager.logout()
            load()
        }
    }
}

expect fun ByteArray.toImageBitmap(): ImageBitmap
