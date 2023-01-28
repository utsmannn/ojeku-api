package com.aej.ojekkuapi.user.entity

import com.aej.ojekkuapi.location.entity.Coordinate
import com.fasterxml.jackson.annotation.JsonProperty

data class UserLocation(
    var id: String,
    var coordinate: Coordinate
) {
    @Suppress("SuspiciousVarProperty")
    var coor: List<Double> = emptyList()
        get() = listOf(coordinate.longitude, coordinate.latitude)
}