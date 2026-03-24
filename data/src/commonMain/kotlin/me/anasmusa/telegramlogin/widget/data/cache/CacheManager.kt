package me.anasmusa.telegramlogin.widget.data.cache

import me.anasmusa.telegramlogin.widget.data.PlatformContext

internal expect suspend fun loadCache(
    context: PlatformContext,
): Cache?

internal expect suspend fun saveCache(
    context: PlatformContext,
    cache: Cache,
)
