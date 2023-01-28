package com.aej.ojekkuapi.location.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class Routes(
    val distance: Long,
    @JsonProperty("duration_estimation")
    val durationEstimated: Long,
    val route: List<Coordinate>
)