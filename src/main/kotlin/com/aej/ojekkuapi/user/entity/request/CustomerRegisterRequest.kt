package com.aej.ojekkuapi.user.entity.request

import com.aej.ojekkuapi.user.entity.User

data class CustomerRegisterRequest(
    val username: String,
    val password: String,
) {
    fun mapToUser(): User {
        return User.createNewCustomer(username, password)
    }
}