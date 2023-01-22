package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.UserLocation

interface UserLocationRepository {

    fun updateLocation(userId: String, coordinate: Coordinate): Result<UserLocation>
    fun getUserLocation(userId: String): Result<UserLocation>
    fun findDriverByCoordinate(coordinate: Coordinate): Result<List<UserLocation>>
}