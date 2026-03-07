package me.anasmusa.shared

actual abstract class PlatformContext {
    companion object {
        val INSTANCE = object : PlatformContext() {}
    }
}
