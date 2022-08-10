package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.toResult
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.repository.UserRepository
import com.aej.ojekkuapi.utils.UserUpdater
import com.mongodb.client.MongoCollection
import org.litote.kmongo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    @Autowired
    private val databaseComponent: DatabaseComponent
) : UserRepository {

    private fun getCollection(): MongoCollection<User> {
        return databaseComponent.database.getDatabase("ojeku")
            .getCollection()
    }

    override fun insertUser(user: User): Result<Boolean> {
        val existingUser = getUserByUsername(user.username)
        return if (existingUser.isSuccess) {
            throw OjekuException("user exist!")
        } else {
            getCollection().insertOne(user).wasAcknowledged().toResult()
        }
    }

    override fun getUserById(id: String): Result<User> {
        return getCollection().findOne(User::id eq id).toResult()
    }

    override fun getUserByUsername(username: String): Result<User> {
        return getCollection().findOne(User::username eq username).toResult("user with $username not found!")
    }

    override fun <T> update(id: String, vararg updater: UserUpdater<T>): Result<Boolean> {
        val fields = updater.map {
            setValue(it.property, it.value)
        }.run {
            combine(this)
        }
        return getCollection()
            .updateOne(
                User::id eq id,
                fields
            ).wasAcknowledged().toResult()
    }
}