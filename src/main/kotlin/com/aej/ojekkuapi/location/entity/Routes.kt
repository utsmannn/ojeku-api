package com.aej.ojekkuapi.location.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class Routes(
    @JsonProperty("distance_in_meters")
    val distanceInMeter: Double,
    val route: List<Coordinate>
)