package com.aej.ojekkuapi.user.entity

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.extra.DriverExtras
import com.aej.ojekkuapi.user.entity.extra.Extras
import com.aej.ojekkuapi.user.entity.extra.emptyExtra
import com.aej.ojekkuapi.user.entity.request.UserView
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class User(
    var id: String = "",
    var username: String = "",
    var password: String? = "",
    var role: Role = Role.CUSTOMER,
    @JsonProperty("fcm_token")
    var fcmToken: String = "",
    var extra: Any = emptyExtra(),
    @JsonProperty("last_location")
    var lastLocation: List<Double> = listOf(0.0, 0.0)
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

    fun toUserView(): UserView {
        return UserView(
            id, username, password, role, fcmToken, extra, Coordinate(lastLocation[1], lastLocation[0])
        )
    }
}