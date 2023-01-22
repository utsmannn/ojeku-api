package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.user.entity.User

interface UserRepository {

    fun insertUser(user: User): Result<Boolean>
    fun getUserById(id: String): Result<User>
    fun getUserByUsername(username: String): Result<User>
    fun updateFcmToken(id: String, fcmToken: String): Result<User>
    fun updateDriverActive(id: String, isDriverActive: Boolean): Result<User>
}