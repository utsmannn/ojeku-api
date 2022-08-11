package com.aej.ojekkuapi.transaction.controller

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.utils.extensions.toResponses
import com.aej.ojekkuapi.transaction.entity.Transaction
import com.aej.ojekkuapi.transaction.entity.TransactionDisplay
import com.aej.ojekkuapi.transaction.entity.TransactionRequest
import com.aej.ojekkuapi.transaction.services.TransactionServices
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.services.UserServices
import com.aej.ojekkuapi.utils.extensions.findUserId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/transaction")
class TransactionController {

    @Autowired
    private lateinit var transactionServices: TransactionServices

    @Autowired
    private lateinit var userServices: UserServices

    @PostMapping("/customer/request")
    fun requestTransaction(
        @RequestBody request: TransactionRequest
    ): BaseResponse<TransactionDisplay> {
        val userId = findUserId().orEmpty()
        return userServices.validateRole(userId, User.Role.CUSTOMER).map {
            transactionServices.requestTransaction(userId, request).getOrThrow()
                .mapToDisplay(userServices)
        }.toResponses()
    }


}