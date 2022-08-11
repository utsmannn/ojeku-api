package com.aej.ojekkuapi.transaction.repository

import com.aej.ojekkuapi.transaction.entity.Transaction
import com.aej.ojekkuapi.utils.DataQuery

interface TransactionRepository {

    fun insertTransaction(transaction: Transaction): Result<Boolean>

    fun getTransactionById(id: String): Result<Transaction>

    fun getTransactionByUserId(userId: String): Result<List<Transaction>>

    fun <T> update(id: String, vararg updater: DataQuery<Transaction, T>): Result<Boolean>
    fun <T> update(filter: List<DataQuery<Transaction, *>>, vararg updater: DataQuery<Transaction, T>): Result<Boolean>
}