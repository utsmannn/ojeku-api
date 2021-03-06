package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.user.entity.User

interface UserRepository {

    fun insertUser(user: User): Result<Boolean>

    fun getUserById(id: String): Result<User>

    fun getUserByUsername(username: String): Result<User>
}