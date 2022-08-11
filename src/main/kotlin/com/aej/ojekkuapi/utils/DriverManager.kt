package com.aej.ojekkuapi.utils

import com.aej.ojekkuapi.location.services.LocationServices
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.extra.DriverExtras
import com.aej.ojekkuapi.user.services.UserServices
import com.aej.ojekkuapi.utils.extensions.safeCastTo

class DriverManager(
    private val userServices: UserServices,
    private val locationServices: LocationServices
) {

    private fun findDriver(userId: String): Result<List<User>> {
        val userCoordinate = userServices.getUserByUserId(userId)
            .map {
                it.coordinate
            }.getOrThrow()
        return locationServices.findDriverFromCoordinate(userCoordinate)
    }

    fun findDriversToken(userId: String): Result<List<String>> {
        return findDriver(userId).map {
            it.filter { user ->
                user.role == User.Role.DRIVER
            }.map { user ->
                user.extra.safeCastTo(DriverExtras::class.java)
                    .fcmToken
            }
        }
    }

    fun getDriverToken(driverId: String): Result<String> {
        return userServices.validateRole(driverId, User.Role.DRIVER)
            .map {
                userServices.getUserByUserId(driverId).getOrThrow()
                    .extra.safeCastTo(DriverExtras::class.java)
                    .fcmToken
            }
    }

    fun getCustomerToken(driverId: String): Result<String> {
        return userServices.validateRole(driverId, User.Role.CUSTOMER)
            .map {
                userServices.getUserByUserId(driverId).getOrThrow()
                    .extra.safeCastTo(DriverExtras::class.java)
                    .fcmToken
            }
    }
}