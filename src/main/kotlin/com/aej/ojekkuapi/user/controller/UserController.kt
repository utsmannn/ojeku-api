package com.aej.ojekkuapi.user.controller

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.toResponses
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.UserLogin
import com.aej.ojekkuapi.user.services.UserServices
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserRequest
import com.aej.ojekkuapi.user.entity.extra.asCustomerExtras
import com.aej.ojekkuapi.user.entity.extra.isCustomerExtras
import com.aej.ojekkuapi.user.entity.extra.isDriverExtras
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController {

    @Autowired
    private lateinit var userServices: UserServices

    @GetMapping
    fun getUser(): BaseResponse<User> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return userServices.getUserByUserId(userId.orEmpty()).toResponses()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody userLogin: UserLogin
    ): BaseResponse<LoginResponse> {
        return userServices.login(userLogin).toResponses()
    }

    @PostMapping("/driver/register")
    fun registerDriver(
        @RequestBody userRequest: UserRequest
    ): BaseResponse<Boolean> {
        val isDriver = userRequest.extra.isDriverExtras()
        if (isDriver) {
            return userServices.register(userRequest.mapToNewDriver()).toResponses()
        } else {
            throw OjekuException("Need `vehiclesNumber` in `extras`")
        }
    }

    @PostMapping("/customer/register")
    fun registerCustomer(
        @RequestBody userRequest: UserRequest
    ): BaseResponse<Boolean> {
        val isCustomer = userRequest.extra.isCustomerExtras()
        if (isCustomer) {
            return userServices.register(userRequest.mapToNewDriver()).toResponses()
        } else {
            throw OjekuException("Role not allow in `customer`")
        }
    }
}