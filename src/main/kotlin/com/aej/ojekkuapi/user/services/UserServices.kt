package com.aej.ojekkuapi.user.services

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserLocation
import com.aej.ojekkuapi.user.entity.UserLogin
import java.util.StringJoiner

interface UserServices {
    fun login(userLogin: UserLogin): Result<LoginResponse>
    fun register(user: User): Result<Boolean>
    fun getUserByUserId(id: String): Result<User>
    fun getUserByUsername(username: String): Result<User>
    fun updateFcmToken(id: String, fcmToken: String): Result<User>
    fun updateDriverActive(id: String, isDriverActive: Boolean): Result<User>
    fun updateUserLocation(id: String, coordinate: Coordinate): Result<UserLocation>
    fun getUserLocation(id: String): Result<UserLocation>
    fun findDriverByCoordinate(coordinate: Coordinate): Result<List<User>>
}