package com.aej.ojekkuapi.user.entity

import com.aej.ojekkuapi.user.entity.extra.DriverExtras

interface UserRequest {
    fun mapToUser(): User

    data class DriverRequest(
        val username: String,
        val password: String,
        val vehiclesNumber: String
    ) : UserRequest {
        override fun mapToUser(): User {
            val driverExtras = DriverExtras(vehiclesNumber)
            return User.createNewDriver(username, password, driverExtras)
        }
    }

    data class CustomerRequest(
        val username: String,
        val password: String
    ) : UserRequest {
        override fun mapToUser(): User {
            return User.createNewCustomer(username, password)
        }
    }
}