package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.utils.DataQuery

interface UserRepository {

    fun insertUser(user: User): Result<Boolean>

    fun getUserById(id: String): Result<User>

    fun getUserByUsername(username: String): Result<User>

    fun <T> update(id: String, vararg updater: DataQuery<User, T>): Result<Boolean>

    fun findDriversByCoordinate(coordinate: Coordinate): Result<List<User>>
}