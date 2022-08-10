package com.aej.ojekkuapi.user.controller

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.toResponses
import com.aej.ojekkuapi.user.entity.LoginResponse
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.entity.UserLogin
import com.aej.ojekkuapi.user.entity.UserRequest
import com.aej.ojekkuapi.user.services.UserServices
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

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

    /**
     * Driver auth
     * */
    @PostMapping("/driver/register")
    fun registerDriver(
        @RequestBody userRequest: UserRequest.DriverRequest
    ): BaseResponse<Boolean> {
        return userServices.register(userRequest.mapToUser()).toResponses()
    }
    /**
     * END Driver auth
     * */

    /**
     * Customer auth
     * */
    @PostMapping("/customer/register")
    fun registerCustomer(
        @RequestBody userRequest: UserRequest.CustomerRequest
    ): BaseResponse<Boolean> {
        return userServices.register(userRequest.mapToUser()).toResponses()
    }
    /**
     * END Customer auth
     * */
}