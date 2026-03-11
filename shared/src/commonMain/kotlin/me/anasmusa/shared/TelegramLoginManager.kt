package me.anasmusa.shared

import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlHandler
import com.mohamedrejeb.ksoup.html.parser.KsoupHtmlParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.anasmusa.shared.cache.loadCache
import me.anasmusa.shared.cache.saveCache

object TelegramLoginManager {
    fun getButtonContent(
        context: PlatformContext,
        config: TelegramLoginConfig,
    ): Flow<TelegramButtonContent> =
        flow {
            val cache = loadCache(context)
            if (cache != null) {
                emit(
                    cache.toButtonContent(
                        text = cache.buttonText.takeIf { cache.languageCode == config.languageCode },
                    ),
                )
            }

            getButtonHtml(config)
                ?.let {
                    parseWidgetHtml(it)
                }?.let { content ->
                    if (content.userPhotoUrl == cache?.userPhotoUrl) {
                        saveCache(
                            context = context,
                            cache =
                                content.toCache(
                                    languageCode = config.languageCode,
                                    userPhotoData = cache?.userPhotoData,
                                ),
                        )
                        emit(content.copy(userPhotoData = cache?.userPhotoData))
                    } else {
                        emit(
                            content.copy(
                                userPhotoData = cache?.userPhotoData?.takeIf { content.userPhotoUrl == cache.userPhotoUrl },
                                dataLoadState = DataLoadState.IN_PROGRESS,
                            ),
                        )

                        val photoData = content.userPhotoUrl?.let { loadImage(it) }
                        saveCache(
                            context = context,
                            cache =
                                content.toCache(
                                    languageCode = config.languageCode,
                                    userPhotoData = photoData,
                                ),
                        )
                        emit(content.copy(userPhotoData = photoData))
                    }
                } ?: run {
                if (cache != null) {
                    emit(
                        cache.toButtonContent(
                            text = cache.buttonText.takeIf { cache.languageCode == config.languageCode },
                            dataLoadState = DataLoadState.FINISHED,
                        ),
                    )
                }
            }
        }

    suspend fun getCookies(): String? = getTelegramCookies()

    suspend fun logout() {
        clearTelegramCookies()
    }

    private fun parseWidgetHtml(string: String): TelegramButtonContent? =
        try {
            val loginText = StringBuilder()
            var userFirstName: String? = null
            var userPhotoUrl: String? = null

            val tags: MutableList<String> = ArrayList()
            val parser =
                KsoupHtmlParser(
                    handler =
                        KsoupHtmlHandler
                            .Builder()
                            .onOpenTag { name, attributes, _ ->
                                if (name == "img") {
                                    userPhotoUrl = attributes["src"]
                                }
                                tags.add(name)
                            }.onCloseTag { _, _ ->
                                tags.removeLast()
                            }.onText {
                                if (tags.contains("button")) {
                                    if (tags.lastOrNull() == "span") {
                                        if (userFirstName == null && it.isNotBlank()) {
                                            userFirstName = it
                                        }
                                    }
                                    if (it.isNotBlank()) {
                                        loginText.append(it)
                                    }
                                }
                            }.build(),
                )
            parser.write(string)
            parser.end()

            TelegramButtonContent(
                text = loginText.takeIf { it.isNotEmpty() }?.toString(),
                userFirstName = userFirstName,
                userPhotoUrl = userPhotoUrl,
                userPhotoData = null,
            )
        } catch (_: Exception) {
            null
        }
}
