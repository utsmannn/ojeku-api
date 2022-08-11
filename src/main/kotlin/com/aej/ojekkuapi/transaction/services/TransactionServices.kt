package com.aej.ojekkuapi.transaction.services

import com.aej.ojekkuapi.transaction.entity.Transaction
import com.aej.ojekkuapi.transaction.entity.TransactionRequest

interface TransactionServices {

    /**
     * Customer
     * */
    fun requestTransaction(userId: String, request: TransactionRequest): Result<Transaction>
    fun getTransactionByCustomer(userId: String): Result<List<Transaction>>

    /**
     * Driver
     * */
    fun acceptTransaction(id: String, driverId: String): Result<Transaction>
    fun declineTransaction(id: String, driverId: String): Result<Transaction>
    fun getTransactionByDriver(userId: String): Result<List<Transaction>>
}