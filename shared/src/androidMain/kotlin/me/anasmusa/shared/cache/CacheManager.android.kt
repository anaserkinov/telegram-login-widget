package me.anasmusa.shared.cache

import me.anasmusa.shared.PlatformContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

internal actual suspend fun loadCache(
    context: PlatformContext,
): Cache? {
    return try {
        val cacheDir = context.cacheDir
        val cacheFile = File(cacheDir, "telegram_login_cache")
        if (cacheFile.exists()) {
            val cacheBuffer = ByteBuffer.wrap(cacheFile.readBytes())
            cacheBuffer.getInt() // version
            val buttonText = cacheBuffer.getNextString()
            val userFirstName = cacheBuffer.getNextString()
            val userPhotoUrl = cacheBuffer.getNextString()
            val languageCode = cacheBuffer.getNextString()

            val imageDataLength = cacheBuffer.limit() - cacheBuffer.position()
            return Cache(
                buttonText = buttonText,
                userFirstName = userFirstName.takeIf { it.isNotEmpty() },
                userPhotoUrl = userPhotoUrl.takeIf { it.isNotEmpty() },
                languageCode = languageCode,
                userPhotoData =
                    if (imageDataLength == 0) {
                        null
                    } else {
                        ByteArray(imageDataLength).apply {
                            cacheBuffer.get(this)
                        }
                    },
            )
        }
        null
    } catch (_: Exception) {
        null
    }
}

internal actual suspend fun saveCache(
    context: PlatformContext,
    cache: Cache,
) {
    val buttonTextBytes = cache.buttonText.toByteArray()
    val userFirstNameBytes = (cache.userFirstName ?: "").toByteArray()
    val userPhotoUrlBytes = (cache.userPhotoUrl ?: "").toByteArray()
    val languageBytes = cache.languageCode.toByteArray()

    val cacheBuffer =
        ByteBuffer
            .allocate(
                4 + // version
                    4 + buttonTextBytes.size + // buttonText size + content
                    4 + userFirstNameBytes.size + // firstName size + content
                    4 + userPhotoUrlBytes.size + // photoUrl size + content
                    4 + languageBytes.size, // lang size + content
            ).apply {
                putInt(1)
                putInt(buttonTextBytes.size)
                put(buttonTextBytes)
                putInt(userFirstNameBytes.size)
                put(userFirstNameBytes)
                putInt(userPhotoUrlBytes.size)
                put(userPhotoUrlBytes)
                putInt(languageBytes.size)
                put(languageBytes)
            }

    val cacheDir = context.cacheDir
    cacheDir.mkdirs()

    val cacheFile = File(cacheDir, "telegram_login_cache")
    FileOutputStream(cacheFile).use { stream ->
        stream.write(cacheBuffer.array())
        cache.userPhotoData?.let {
            stream.write(it)
        }
    }
}

private fun ByteBuffer.getNextString(): String {
    val length = getInt()
    return ByteArray(length).let { bytes ->
        get(
            bytes,
            0,
            length,
        )
        bytes.toString(Charsets.UTF_8)
    }
}
