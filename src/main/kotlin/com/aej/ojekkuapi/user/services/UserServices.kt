package com.aej.ojekkuapi.user.services

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserLocation
import com.aej.ojekkuapi.user.entity.UserLogin
import com.aej.ojekkuapi.user.entity.request.UserView
import java.util.StringJoiner

interface UserServices {
    suspend fun login(userLogin: UserLogin): Result<LoginResponse>
    suspend fun register(user: User): Result<Boolean>
    suspend fun getUserByUserId(id: String): Result<UserView>
    suspend fun getUserByUserIdAndRole(id: String, role: User.Role): Result<UserView>
    suspend fun getUserByUsername(username: String): Result<UserView>
    suspend fun updateFcmToken(id: String, fcmToken: String): Result<UserView>
    suspend fun updateDriverActive(id: String, isDriverActive: Boolean): Result<UserView>
    suspend fun updateUserLocation(id: String, coordinate: Coordinate): Result<UserView>
    suspend fun findDriverByCoordinate(coordinate: Coordinate): Result<List<UserView>>
}