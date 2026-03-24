package me.anasmusa.telegramlogin.widget.data.cache

import me.anasmusa.telegramlogin.widget.data.DataLoadState
import me.anasmusa.telegramlogin.widget.data.TelegramButtonContent

internal class Cache(
    val buttonText: String,
    val userFirstName: String?,
    val userPhotoUrl: String?,
    val languageCode: String,
    val userPhotoData: ByteArray?,
) {
    fun toButtonContent(
        text: String? = this.buttonText,
        userFirstName: String? = this.userFirstName,
        userPhotoUrl: String? = this.userPhotoUrl,
        userPhotoData: ByteArray? = this.userPhotoData,
        dataLoadState: DataLoadState = DataLoadState.IN_PROGRESS,
    ) = TelegramButtonContent(
        text = text,
        userFirstName = userFirstName,
        userPhotoUrl = userPhotoUrl,
        userPhotoData = userPhotoData,
        dataLoadState = dataLoadState,
    )
}
