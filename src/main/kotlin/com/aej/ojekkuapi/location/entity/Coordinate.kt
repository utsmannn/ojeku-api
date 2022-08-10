package com.aej.ojekkuapi.location.entity

data class Coordinate(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) {
    override fun toString(): String {
        return "$latitude,$longitude"
    }
}