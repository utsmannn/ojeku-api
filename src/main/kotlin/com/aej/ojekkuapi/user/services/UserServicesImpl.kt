package com.aej.ojekkuapi.user.services

import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.authentication.JwtConfig
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserLocation
import com.aej.ojekkuapi.user.entity.UserLogin
import com.aej.ojekkuapi.user.entity.extra.DriverExtras
import com.aej.ojekkuapi.user.repository.UserLocationRepository
import com.aej.ojekkuapi.user.repository.UserRepository
import com.aej.ojekkuapi.utils.safeCastTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserServicesImpl(
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val userLocationRepository: UserLocationRepository
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

    override fun updateDriverActive(id: String, isDriverActive: Boolean): Result<User> {
        return userRepository.updateDriverActive(id, isDriverActive)
    }

    override fun updateUserLocation(id: String, coordinate: Coordinate): Result<UserLocation> {
        return userLocationRepository.updateLocation(id, coordinate)
    }

    override fun getUserLocation(id: String): Result<UserLocation> {
        return userLocationRepository.getUserLocation(id)
    }

    override fun findDriverByCoordinate(coordinate: Coordinate): Result<List<User>> {
        val userLocationList = userLocationRepository.findDriverByCoordinate(coordinate)
            .map {
                it.map {
                    userRepository.getUserById(it.id).getOrNull()
                }.filterNotNull()
                    .filter { it.role == User.Role.DRIVER }
                    .filter { it.extra.safeCastTo(DriverExtras::class.java).isActive }
            }

        return userLocationList
    }
}