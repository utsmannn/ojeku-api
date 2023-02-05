package com.aej.ojekkuapi.user.socket

import com.fasterxml.jackson.annotation.JsonProperty

data class UserRequest(
    @JsonProperty("username")
    var username: String
)