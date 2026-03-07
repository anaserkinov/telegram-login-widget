package me.anasmusa.shared.cache

import me.anasmusa.shared.PlatformContext

internal expect suspend fun loadCache(
    context: PlatformContext,
): Cache?

internal expect suspend fun saveCache(
    context: PlatformContext,
    cache: Cache,
)
