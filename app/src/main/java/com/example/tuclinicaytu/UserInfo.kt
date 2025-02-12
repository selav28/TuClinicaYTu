package com.example.tuclinicaytu.model

data class UserInfo(
    val uid: String,
    val email: String?,
    val providers: List<String>,
    val creationTimestamp: Long?,
    val lastSignInTimestamp: Long?
)