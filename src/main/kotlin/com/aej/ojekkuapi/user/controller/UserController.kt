package com.aej.ojekkuapi.user.controller

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.coordinateStringToData
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.toResponses
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.UserLogin
import com.aej.ojekkuapi.user.services.UserServices
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserLocation
import com.aej.ojekkuapi.user.entity.request.CustomerRegisterRequest
import com.aej.ojekkuapi.user.entity.request.DriverRegisterRequest
import com.aej.ojekkuapi.user.entity.request.UpdateFcmToken
import com.aej.ojekkuapi.user.entity.request.UserView
import com.aej.ojekkuapi.user.socket.RouteLocationSocket
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController {

    @Autowired
    private lateinit var userServices: UserServices

    @Autowired
    private lateinit var routeLocationSocket: RouteLocationSocket

    @GetMapping
    suspend fun getUser(): BaseResponse<UserView> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return userServices.getUserByUserId(userId.orEmpty()).toResponses()
    }

    @PostMapping("/login")
    suspend fun login(
        @RequestBody userLogin: UserLogin
    ): BaseResponse<LoginResponse> {
        return userServices.login(userLogin).toResponses()
    }

    @PutMapping("/fcm")
    suspend fun updateFcmToken(
        @RequestBody updateFcmToken: UpdateFcmToken
    ): BaseResponse<UserView> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return userServices.updateFcmToken(userId.orEmpty(), updateFcmToken.fcm).toResponses()
    }

    @PutMapping("/location")
    suspend fun updateLocation(
        @RequestParam(value = "coordinate", required = true) coordinateString: String
    ): BaseResponse<UserView> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        val coordinate = coordinateString.coordinateStringToData()
        return userServices.updateUserLocation(userId.orEmpty(), coordinate).toResponses()
    }

    @PostMapping("/driver/register")
    suspend fun registerDriver(
        @RequestBody userRequest: DriverRegisterRequest
    ): BaseResponse<Boolean> {
        val user = userRequest.mapToUser()
        return userServices.register(user).toResponses()
    }

    @PostMapping("/driver/active")
    suspend fun postDriverActive(
        @RequestParam(value = "is_active", required = true) isActive: Boolean
    ): BaseResponse<UserView> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return userServices.updateDriverActive(userId.orEmpty(), isActive).toResponses()
    }

    @GetMapping("/driver/{user_id}")
    suspend fun getDriverByUserId(
        @PathVariable(value = "user_id") userId: String
    ): BaseResponse<UserView> {
        return userServices.getUserByUserIdAndRole(userId, User.Role.DRIVER).toResponses()
    }

    @PostMapping("/customer/register")
    suspend fun registerCustomer(
        @RequestBody userRequest: CustomerRegisterRequest
    ): BaseResponse<Boolean> {
        val user = userRequest.mapToUser()
        return userServices.register(user).toResponses()
    }

    @GetMapping("/customer/{user_id}")
    suspend fun getCustomerByUserId(
        @PathVariable(value = "user_id") userId: String
    ): BaseResponse<UserView> {
        return userServices.getUserByUserIdAndRole(userId, User.Role.CUSTOMER).toResponses()
    }

    @PostMapping("/socket/test")
    suspend fun testSocket(): String {
        routeLocationSocket.send("postman", "data dari backend")
        return "ok"
    }
}