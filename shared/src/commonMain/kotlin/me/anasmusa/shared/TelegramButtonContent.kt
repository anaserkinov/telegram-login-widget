package me.anasmusa.shared

import me.anasmusa.shared.cache.Cache

class TelegramButtonContent(
    val text: String?,
    val userFirstName: String?,
    val userPhotoUrl: String?,
    val userPhotoData: ByteArray?,
    val dataLoadState: DataLoadState = DataLoadState.FINISHED,
) {
    fun copy(
        text: String? = this.text,
        userFirstName: String? = this.userFirstName,
        userPhotoUrl: String? = this.userPhotoUrl,
        userPhotoData: ByteArray? = this.userPhotoData,
        dataLoadState: DataLoadState = this.dataLoadState,
    ) = TelegramButtonContent(
        text = text,
        userFirstName = userFirstName,
        userPhotoUrl = userPhotoUrl,
        userPhotoData = userPhotoData,
        dataLoadState = dataLoadState,
    )

    internal fun toCache(
        languageCode: String,
        userPhotoData: ByteArray?,
    ) = Cache(
        buttonText = text ?: "",
        userFirstName = userFirstName,
        userPhotoUrl = userPhotoUrl,
        languageCode = languageCode,
        userPhotoData = userPhotoData,
    )
}
