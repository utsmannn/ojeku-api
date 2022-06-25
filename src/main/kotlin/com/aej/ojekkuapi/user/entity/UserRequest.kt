package com.aej.ojekkuapi.user.entity

data class UserRequest(
    val username: String,
    val password: String
) {
    fun mapToNewUser(): User {
        return User.createNewUser(username, password)
    }
}