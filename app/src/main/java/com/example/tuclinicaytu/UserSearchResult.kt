package com.example.tuclinicaytu

import com.example.tuclinicaytu.model.UserInfo

sealed class UserSearchResult {
    data class Success(val users: List<UserInfo>) : UserSearchResult()
    data class Error(val message: String) : UserSearchResult()
}