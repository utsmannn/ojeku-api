package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.toResult
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.extra.CustomerExtras
import com.aej.ojekkuapi.user.entity.extra.DriverExtras
import com.aej.ojekkuapi.user.repository.UserRepository
import com.aej.ojekkuapi.utils.safeCastTo
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
        return getCollection().findOne(User::id eq id).run {
            if (this?.role == User.Role.DRIVER) {
                this.extra.safeCastTo(DriverExtras::class.java)
            } else {
                this?.extra?.safeCastTo(CustomerExtras::class.java)
            }
            this
        }.toResult()
    }

    override fun getUserByUsername(username: String): Result<User> {
        return getCollection().findOne(User::username eq username).toResult("user with $username not found!")
    }

    override fun updateFcmToken(id: String, fcmToken: String): Result<User> {
        getCollection().updateOne(User::id eq id, User::fcmToken setTo fcmToken).toResult()
        return getUserById(id)
    }
}