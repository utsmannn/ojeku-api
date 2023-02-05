package com.aej.ojekkuapi.user.services

import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.authentication.JwtConfig
import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.repository.BookingRepository
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.services.LocationServices
import com.aej.ojekkuapi.messaging.MessagingComponent
import com.aej.ojekkuapi.messaging.entity.FcmMessage
import com.aej.ojekkuapi.toResult
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserLogin
import com.aej.ojekkuapi.user.entity.request.UserView
import com.aej.ojekkuapi.user.repository.UserRepository
import com.aej.ojekkuapi.user.socket.RouteLocationSocket
import com.aej.ojekkuapi.utils.toJson
import org.litote.kmongo.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServicesImpl(
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val bookingRepository: BookingRepository,
    @Autowired
    private val messagingComponent: MessagingComponent,
    @Autowired
    private val locationServices: LocationServices,
    @Autowired
    private val routeLocationSocket: RouteLocationSocket
) : UserServices {

    init {
        routeLocationSocket.init(userRepository)
    }

    override suspend fun login(userLogin: UserLogin): Result<LoginResponse> {
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

    override suspend fun register(user: User): Result<Boolean> {
        return userRepository.insertUser(user)
    }

    override suspend fun getUserByUserId(id: String): Result<UserView> {
        return userRepository.getUserById(id).map {
            it.password = null
            it
        }.map { it.toUserView() }
    }

    override suspend fun getUserByUserIdAndRole(id: String, role: User.Role): Result<UserView> {
        val currentUser = getUserByUserId(id).getOrThrow()
        if (currentUser.role != role) throw OjekuException("Forbidden by user role!")
        return currentUser.toResult()
    }

    override suspend fun getUserByUsername(username: String): Result<UserView> {
        return userRepository.getUserByUsername(username).map {
            it.password = null
            it
        }.map { it.toUserView() }
    }

    override suspend fun updateFcmToken(id: String, fcmToken: String): Result<UserView> {
        return userRepository.updateFcmToken(id, fcmToken).map {
            it.password = null
            it
        }.map { it.toUserView() }
    }

    override suspend fun updateDriverActive(id: String, isDriverActive: Boolean): Result<UserView> {
        return userRepository.updateDriverActive(id, isDriverActive).map { it.toUserView() }
    }

    override suspend fun updateUserLocation(id: String, coordinate: Coordinate): Result<UserView> {
        val updatedUserResult = userRepository.updateUserLocation(id, coordinate).map { it.toUserView() }
        val currentUser = updatedUserResult.getOrThrow()
        if (currentUser.role == User.Role.DRIVER) {

            val currentBooking = bookingRepository.getBookingByDriverIdAndStatus(
                currentUser.id,
                Booking.BookingStatus.ACCEPTED
            ).getOrNull() ?: bookingRepository.getBookingByDriverIdAndStatus(
                currentUser.id,
                Booking.BookingStatus.ONGOING
            ).getOrNull()

            val customerId = currentBooking?.customerId
            if (customerId != null) {
                val currentCustomer = userRepository.getUserById(customerId).getOrNull()
                if (currentCustomer != null) {
                    val otherCoordinate = if (currentBooking.status == Booking.BookingStatus.ACCEPTED) {
                        currentBooking.routeLocation.from.coordinate
                    } else {
                        currentBooking.routeLocation.destination.coordinate
                    }

                    val route = locationServices.getRoutesLocation(coordinate, otherCoordinate).getOrNull()
                    val message = mapOf(
                        "driver" to coordinate,
                        "route" to route
                    )

                    routeLocationSocket.send(currentCustomer.username, message.json)
                    routeLocationSocket.send(currentUser.username, message.json)
                }
            }
        }
        return updatedUserResult
    }

    override suspend fun findDriverByCoordinate(coordinate: Coordinate): Result<List<UserView>> {
        val userLocationList = userRepository.findDriverByCoordinates(coordinate)
        return userLocationList.map { it.map { it.toUserView() } }
    }
}