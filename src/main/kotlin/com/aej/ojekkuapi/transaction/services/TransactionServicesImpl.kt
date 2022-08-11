package com.aej.ojekkuapi.transaction.services

import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.location.services.LocationServices
import com.aej.ojekkuapi.transaction.entity.Transaction
import com.aej.ojekkuapi.transaction.entity.TransactionRequest
import com.aej.ojekkuapi.transaction.repository.TransactionRepository
import com.aej.ojekkuapi.user.services.UserServices
import com.aej.ojekkuapi.utils.DriverManager
import com.aej.ojekkuapi.utils.extensions.to
import com.aej.ojekkuapi.utils.fcm.FirebaseMessage
import com.aej.ojekkuapi.utils.fcm.Messaging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionServicesImpl(
    @Autowired
    private val transactionRepository: TransactionRepository,
    @Autowired
    private val locationServices: LocationServices,
    @Autowired
    private val userServices: UserServices
) : TransactionServices {

    private val driverManager: DriverManager by lazy {
        DriverManager(
            userServices, locationServices
        )
    }

    override fun requestTransaction(userId: String, request: TransactionRequest): Result<Transaction> {
        // check current waiting and process transaction
        val existingTransactionStatus = transactionRepository
            .getTransactionByUserId(userId).getOrDefault(emptyList())
            .map { it.status }

        val waitingTransaction = existingTransactionStatus.filter { it == Transaction.TransactionStatus.WAITING }
        val processTransaction = existingTransactionStatus.filter { it == Transaction.TransactionStatus.PROCESS }

        val isTransactionAllow = waitingTransaction.isEmpty() && processTransaction.isEmpty()

        if (!isTransactionAllow) {
            throw OjekuException("Transaction decline, you must finish other transaction!")
        }

        // create new transaction object
        val transaction = Transaction.createNewTransaction(userId, request, locationServices)

        // find all drivers in 3KM
        val driversToken = driverManager.findDriversToken(userId).getOrThrow().ifEmpty {
            throw OjekuException("Driver not found!")
        }

        // send fcm to broadcast to drivers
        val message = FirebaseMessage.createTransaction(transaction)
        return Messaging.broadcastMessage(message, *driversToken.toTypedArray()).onSuccess {
            transactionRepository.insertTransaction(transaction)
        }.map {
            transactionRepository.getTransactionById(transaction.id).getOrThrow()
        }
    }

    override fun getTransactionByCustomer(userId: String): Result<List<Transaction>> {
        return transactionRepository.getTransactionByUserId(userId)
    }

    override fun acceptTransaction(id: String, driverId: String): Result<Transaction> {
        val currentTransaction = transactionRepository.getTransactionById(id).getOrThrow()

        if (currentTransaction.driverId != null) {
            throw OjekuException("Accept failed!")
        }

        val filterId = Transaction::id to id
        val filterStatus = Transaction::status to Transaction.TransactionStatus.WAITING
        val filtered = listOf(filterId, filterStatus)

        transactionRepository.update(
            filtered,
            Transaction::driverId to driverId,
            Transaction::status to Transaction.TransactionStatus.PROCESS
        )
        return transactionRepository.getTransactionById(id).map { transaction ->
            val message = FirebaseMessage.createTransaction(transaction)
            val token = driverManager.getCustomerToken(currentTransaction.customerId).getOrThrow()
            Messaging.broadcastMessage(message, token).onFailure {
                throw OjekuException("Accept failed!")
            }
            transaction
        }
    }

    override fun declineTransaction(id: String, driverId: String): Result<Transaction> {
        // send fcm to broadcast to next driver
        //return transactionRepository.getTransactionById(id)
        val currentTransaction = transactionRepository.getTransactionById(id).getOrThrow()

        if (currentTransaction.driverId != null) {
            throw OjekuException("Decline failed!")
        }

        return transactionRepository.getTransactionById(id).map { transaction ->
            val message = FirebaseMessage.createTransaction(transaction)
            val token = driverManager.getCustomerToken(currentTransaction.customerId).getOrThrow()
            Messaging.broadcastMessage(message, token).onFailure {
                throw OjekuException("Accept failed!")
            }
            transaction
        }
    }

    override fun getTransactionByDriver(userId: String): Result<List<Transaction>> {
        return transactionRepository.getTransactionByUserId(userId)
    }
}