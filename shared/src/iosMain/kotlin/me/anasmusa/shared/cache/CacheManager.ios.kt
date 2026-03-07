package me.anasmusa.shared.cache

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import me.anasmusa.shared.PlatformContext
import me.anasmusa.shared.toByteArray
import me.anasmusa.shared.toNSData
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual suspend fun loadCache(context: PlatformContext): Cache? {
    return try {
        val cacheFile = getCacheFilePath() ?: return null

        if (!NSFileManager.defaultManager.fileExistsAtPath(cacheFile)) return null

        val nsData = NSData.dataWithContentsOfFile(cacheFile) ?: return null
        val bytes = nsData.toByteArray()
        val buffer = ByteBuffer(bytes)

        buffer.getInt() // version
        val buttonText = buffer.getNextString()
        val userFirstName = buffer.getNextString()
        val userPhotoUrl = buffer.getNextString()
        val languageCode = buffer.getNextString()

        val imageDataLength = bytes.size - buffer.position
        Cache(
            buttonText = buttonText,
            userFirstName = userFirstName.takeIf { it.isNotEmpty() },
            userPhotoUrl = userPhotoUrl.takeIf { it.isNotEmpty() },
            languageCode = languageCode,
            userPhotoData =
                if (imageDataLength == 0) {
                    null
                } else {
                    buffer.getBytes(imageDataLength)
                },
        )
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
internal actual suspend fun saveCache(
    context: PlatformContext,
    cache: Cache,
) {
    try {
        val cacheFile = getCacheFilePath() ?: return

        val buttonTextBytes = cache.buttonText.encodeToByteArray()
        val userFirstNameBytes = (cache.userFirstName ?: "").encodeToByteArray()
        val userPhotoUrlBytes = (cache.userPhotoUrl ?: "").encodeToByteArray()
        val languageBytes = cache.languageCode.encodeToByteArray()

        val buffer =
            ByteBuffer(
                4 + // version
                    4 + buttonTextBytes.size + // buttonText size + content
                    4 + userFirstNameBytes.size + // firstName size + content
                    4 + userPhotoUrlBytes.size + // photoUrl size + content
                    4 + languageBytes.size, // lang size + content
            ).apply {
                putInt(1)
                putInt(buttonTextBytes.size)
                putBytes(buttonTextBytes)
                putInt(userFirstNameBytes.size)
                putBytes(userFirstNameBytes)
                putInt(userPhotoUrlBytes.size)
                putBytes(userPhotoUrlBytes)
                putInt(languageBytes.size)
                putBytes(languageBytes)
            }

        val allBytes = buffer.array() + (cache.userPhotoData ?: byteArrayOf())
        val nsData = allBytes.toNSData()
        nsData.writeToFile(cacheFile, atomically = true)
    } catch (_: Exception) {
    }
}

// ── Helpers ───────────────────────────────────────────────────────────────────

private fun getCacheFilePath(): String? {
    val paths =
        NSSearchPathForDirectoriesInDomains(
            NSCachesDirectory,
            NSUserDomainMask,
            true,
        )
    val cacheDir = paths.firstOrNull() as? String ?: return null
    return "$cacheDir/telegram_login_cache"
}

// ── Minimal ByteBuffer (mirrors java.nio.ByteBuffer for iOS) ──────────────────

private class ByteBuffer {
    private val data: ByteArray
    var position: Int = 0
        private set

    constructor(capacity: Int) {
        data = ByteArray(capacity)
    }

    constructor(bytes: ByteArray) {
        data = bytes
    }

    fun putInt(value: Int) {
        data[position++] = (value shr 24).toByte()
        data[position++] = (value shr 16).toByte()
        data[position++] = (value shr 8).toByte()
        data[position++] = value.toByte()
    }

    fun getInt(): Int {
        val b0 = data[position++].toInt() and 0xFF
        val b1 = data[position++].toInt() and 0xFF
        val b2 = data[position++].toInt() and 0xFF
        val b3 = data[position++].toInt() and 0xFF
        return (b0 shl 24) or (b1 shl 16) or (b2 shl 8) or b3
    }

    fun putBytes(bytes: ByteArray) {
        bytes.copyInto(data, position)
        position += bytes.size
    }

    fun getBytes(length: Int): ByteArray {
        val result = data.copyOfRange(position, position + length)
        position += length
        return result
    }

    fun array(): ByteArray = data

    fun getNextString(): String {
        val length = getInt()
        return getBytes(length).decodeToString()
    }
}
