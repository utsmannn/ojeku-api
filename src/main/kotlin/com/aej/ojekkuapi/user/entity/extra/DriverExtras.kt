package com.aej.ojekkuapi.user.entity.extra

import com.fasterxml.jackson.annotation.JsonProperty

data class DriverExtras(
    @JsonProperty("vehicles_number")
    var vehiclesNumber: String = ""
) : Extras()