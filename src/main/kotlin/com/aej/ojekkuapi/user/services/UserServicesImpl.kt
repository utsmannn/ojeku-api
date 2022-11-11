package com.aej.ojekkuapi.user.services

import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.authentication.JwtConfig
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserLogin
import com.aej.ojekkuapi.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServicesImpl(
    @Autowired
    private val userRepository: UserRepository
) : UserServices {

    override fun login(userLogin: UserLogin): Result<LoginResponse> {
        val resultUser = userRepository.getUserByUsername(userLogin.username)
        return resultUser.map {
            val token = JwtConfig.generateToken(it)
            val passwordInDb = it.password
            val passwordRequest = userLogin.password
            if (passwordInDb == passwordRequest) {
                LoginResponse(token)
            } else {
                throw OjekuException("Password invalid!")
            }
        }
    }

    override fun register(user: User): Result<Boolean> {
        return userRepository.insertUser(user)
    }

    override fun getUserByUserId(id: String): Result<User> {
        return userRepository.getUserById(id).map {
            it.password = null
            it
        }
    }

    override fun getUserByUsername(username: String): Result<User> {
        return userRepository.getUserByUsername(username).map {
            it.password = null
            it
        }
    }

    override fun updateFcmToken(id: String, fcmToken: String): Result<User> {
        return userRepository.updateFcmToken(id, fcmToken).map {
            it.password = null
            it
        }
    }
}