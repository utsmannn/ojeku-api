package com.aej.ojekkuapi.user.entity

import com.aej.ojekkuapi.location.entity.Coordinate

data class UserLocation(
    var id: String,
    var coordinate: Coordinate
) {

    @Suppress("SuspiciousVarProperty")
    var lngLat: List<Double> = emptyList()
        get() = listOf(coordinate.longitude, coordinate.latitude)
}