package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.utils.extensions.toResult
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.extra.Extras
import com.aej.ojekkuapi.utils.extensions.combineUpdate
import com.aej.ojekkuapi.utils.DataQuery
import com.aej.ojekkuapi.utils.extensions.collection
import com.aej.ojekkuapi.utils.extensions.distanceFrom
import org.litote.kmongo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    @Autowired
    private val databaseComponent: DatabaseComponent
) : UserRepository {

    override fun insertUser(user: User): Result<Boolean> {
        val existingUser = getUserByUsername(user.username)
        return if (existingUser.isSuccess) {
            throw OjekuException("user exist!")
        } else {
            databaseComponent.collection<User>()
                .insertOne(user).wasAcknowledged()
                .toResult()
        }
    }

    override fun getUserById(id: String): Result<User> {
        return databaseComponent.collection<User>()
            .findOne(User::id eq id)
            .toResult()
    }

    override fun getUserByUsername(username: String): Result<User> {
        return databaseComponent.collection<User>()
            .findOne(User::username eq username)
            .toResult("user with $username not found!")
    }

    override fun <T> update(id: String, vararg updater: DataQuery<User, T>): Result<Boolean> {
        val fields = updater.toList().combineUpdate()
        return databaseComponent.collection<User>()
            .updateOne(
                User::id eq id,
                fields
            ).wasAcknowledged()
            .toResult()
    }

    override fun findDriversByCoordinate(coordinate: Coordinate): Result<List<User>> {
        val result = databaseComponent.collection<User>()
            .find(User::role eq User.Role.DRIVER)
            .filter(User::extra / Extras::fcmToken ne "")
            .filter {
                it.coordinate.distanceFrom(coordinate).apply {
                    println("distance from ${it.id} -> $this")
                } <= 3000.0
            }
        return result.toResult()
    }
}