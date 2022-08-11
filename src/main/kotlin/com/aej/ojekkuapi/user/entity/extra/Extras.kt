package com.aej.ojekkuapi.user.entity.extra

import com.fasterxml.jackson.annotation.JsonProperty

open class Extras {
    @JsonProperty("fcm_token")
    var fcmToken: String = ""

    companion object {
        fun emptyExtra() = object : Extras() {}
    }
}
