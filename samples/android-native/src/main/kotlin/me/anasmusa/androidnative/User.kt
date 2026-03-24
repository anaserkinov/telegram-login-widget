package me.anasmusa.androidnative

data class User(
    val photoUrl: String?,
    val fullName: String,
    val username: String?,
    val phoneNumber: String?,
    val loggedInWithWidget: Boolean,
)
