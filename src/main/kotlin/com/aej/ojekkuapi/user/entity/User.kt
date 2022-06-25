package com.aej.ojekkuapi.user.entity

import java.util.*

data class User(
    var id: String = "",
    var username: String = "",
    var password: String? = ""
) {
    companion object {
        fun createNewUser(username: String, password: String): User {
            return User(
                id = UUID.randomUUID().toString(),
                username = username,
                password = password
            )
        }
    }
}