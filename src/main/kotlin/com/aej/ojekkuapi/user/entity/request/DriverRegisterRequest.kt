package com.aej.ojekkuapi.user.entity.request

import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.extra.DriverExtras
import com.fasterxml.jackson.annotation.JsonProperty

data class DriverRegisterRequest(
    val username: String,
    val password: String,
    @JsonProperty("vehicles_number")
    val vehiclesNumber: String
) {
    fun mapToUser(): User {
        val userExtras = DriverExtras(vehiclesNumber)
        return User.createNewDriver(username, password, userExtras)
    }
}