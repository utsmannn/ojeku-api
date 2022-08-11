package com.aej.ojekkuapi.transaction.repository

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.utils.extensions.toResult
import com.aej.ojekkuapi.transaction.entity.Transaction
import com.aej.ojekkuapi.utils.DataQuery
import com.aej.ojekkuapi.utils.extensions.collection
import com.aej.ojekkuapi.utils.extensions.combineUpdate
import org.litote.kmongo.descending
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.or
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class TransactionRepositoryImpl(
    @Autowired
    private val databaseComponent: DatabaseComponent
) : TransactionRepository {
    override fun insertTransaction(transaction: Transaction): Result<Boolean> {
        val existingTransaction = getTransactionById(transaction.id)
        return if (existingTransaction.isSuccess) {
            throw OjekuException("Transaction exist!")
        } else {
            databaseComponent.collection<Transaction>()
                .insertOne(transaction)
                .wasAcknowledged()
                .toResult()
        }
    }

    override fun getTransactionById(id: String): Result<Transaction> {
        return databaseComponent.collection<Transaction>()
            .findOne(Transaction::id eq id)
            .toResult()
    }

    override fun getTransactionByUserId(userId: String): Result<List<Transaction>> {
        val queryCustomerId = Transaction::customerId eq userId
        val queryDriverId = Transaction::driverId eq userId
        return databaseComponent.collection<Transaction>()
            .find(or(queryCustomerId, queryDriverId))
            .sort(descending(Transaction.TransactionDate::requestDate))
            .toList()
            .toResult()
    }

    override fun <T> update(id: String, vararg updater: DataQuery<Transaction, T>): Result<Boolean> {
        val fields = updater.toList().combineUpdate()
        return databaseComponent.collection<Transaction>()
            .updateOne(
                Transaction::id eq id,
                fields
            ).wasAcknowledged()
            .toResult()
    }

    override fun <T> update(
        filter: List<DataQuery<Transaction, *>>,
        vararg updater: DataQuery<Transaction, T>
    ): Result<Boolean> {
        val filtered = filter.combineUpdate()
        val updated = updater.toList().combineUpdate()
        return databaseComponent.collection<Transaction>()
            .updateOne(
                filtered,
                updated
            ).wasAcknowledged()
            .toResult()

    }
}