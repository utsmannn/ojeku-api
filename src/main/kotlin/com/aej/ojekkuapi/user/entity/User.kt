package com.aej.ojekkuapi.user.entity

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.extra.DriverExtras
import com.aej.ojekkuapi.user.entity.extra.Extras
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    var id: String = "",
    var username: String = "",
    var password: String? = "",
    var role: Role = Role.CUSTOMER,
    var coordinate: Coordinate = Coordinate(),
    var extra: Any = Extras.emptyExtra()
) {

    enum class Role {
        CUSTOMER, DRIVER
    }
    companion object {
        fun createNewDriver(username: String, password: String, extra: DriverExtras): User {
            return User(
                id = UUID.randomUUID().toString(),
                username = username,
                password = password,
                role = Role.DRIVER,
                extra = extra
            )
        }

        fun createNewCustomer(username: String, password: String): User {
            return User(
                id = UUID.randomUUID().toString(),
                username = username,
                password = password,
                role = Role.CUSTOMER
            )
        }
    }

    fun mapToUserMini(): UserMini {
        return UserMini(
            username, coordinate
        )
    }

    data class UserMini(
        var username: String = "",
        var coordinate: Coordinate = Coordinate()
    )
}