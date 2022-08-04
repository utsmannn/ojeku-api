package com.aej.ojekkuapi.user.entity

import com.aej.ojekkuapi.user.entity.extra.Extras
import com.aej.ojekkuapi.user.entity.extra.asDriverExtras

data class UserRequest(
    val username: String,
    val password: String,
    val extra: Extras
) {
    fun mapToNewDriver(): User {
        return User.createNewDriver(username, password, extra.asDriverExtras())
    }

    fun mapToNewCustomer(): User {
        return User.createNewCustomer(username, password)
    }
}