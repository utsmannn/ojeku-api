package com.aej.ojekkuapi.location.services

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.entity.Location
import com.aej.ojekkuapi.location.entity.Routes

interface LocationServices {
    fun searchLocation(name: String, coordinate: Coordinate): Result<List<Location>>
    fun reserveLocation(coordinate: Coordinate): Result<Location>
    fun getRoutesLocation(origin: Coordinate, destination: Coordinate): Result<Routes>
}