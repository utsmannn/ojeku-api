package com.aej.ojekkuapi.location.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class Coordinate(
    @JsonProperty("lat")
    val latitude: Double = 0.0,
    @JsonProperty("lng")
    val longitude: Double = 0.0
)