package com.aej.ojekkuapi.user.entity.request

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.extra.emptyExtra
import com.fasterxml.jackson.annotation.JsonProperty

data class UserView(
    var id: String = "",
    var username: String = "",
    var password: String? = "",
    var role: User.Role = User.Role.CUSTOMER,
    @JsonProperty("fcm_token")
    var fcmToken: String = "",
    var extra: Any = emptyExtra(),
    @JsonProperty("last_location")
    var lastLocation: Coordinate = Coordinate()
)