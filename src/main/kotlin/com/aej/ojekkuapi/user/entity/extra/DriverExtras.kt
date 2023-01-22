package com.aej.ojekkuapi.user.entity.extra

import com.fasterxml.jackson.annotation.JsonProperty

data class DriverExtras(
    @JsonProperty("vehicles_number")
    var vehiclesNumber: String = "",
    @JsonProperty("is_active")
    var isActive: Boolean = false
) : Extras()