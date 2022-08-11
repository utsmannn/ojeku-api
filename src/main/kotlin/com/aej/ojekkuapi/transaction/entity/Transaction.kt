package com.aej.ojekkuapi.transaction.entity

import com.aej.ojekkuapi.Constant
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.services.LocationServices
import com.aej.ojekkuapi.user.services.UserServices
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.util.UUID

data class Transaction(
    var id: String = "",
    @JsonProperty("customer_id")
    var customerId: String = "",
    @JsonProperty("driver_id")
    var driverId: String? = null,
    var journey: TransactionJourney = TransactionJourney(),
    var status: TransactionStatus = TransactionStatus.INACTIVE,
    var date: TransactionDate = TransactionDate()
) {

    companion object {
        fun createNewTransaction(
            customerId: String,
            request: TransactionRequest,
            locationServices: LocationServices
        ): Transaction {
            val distance = locationServices.calculateDistance(request.origin, request.destination).getOrThrow()
            val price = ((distance / 1000) * Constant.PRICE_PER_METER).toBigDecimal().toDouble()

            val journey = TransactionJourney(
                origin = request.origin,
                destination = request.destination,
                distance = distance,
                price = price
            )

            return Transaction(
                id = UUID.randomUUID().toString(),
                customerId = customerId,
                journey = journey,
                status = TransactionStatus.WAITING
            )
        }
    }

    data class TransactionDate(
        @JsonProperty("request_date")
        var requestDate: String = Instant.now().toString(),
        @JsonProperty("approval_date")
        var approvalDate: String? = null
    )

    data class TransactionJourney(
        var origin: Coordinate = Coordinate(),
        var destination: Coordinate = Coordinate(),
        var price: Double = 0.0,
        var distance: Double = 0.0
    )

    enum class TransactionStatus {
        WAITING, PROCESS, DONE, INACTIVE
    }

    fun mapToDisplay(userServices: UserServices): TransactionDisplay {
        val customer = userServices.getUserByUserId(customerId).getOrThrow().mapToUserMini()
        val driver = userServices.getUserByUserId(driverId.orEmpty()).getOrNull()?.mapToUserMini()
        return TransactionDisplay(
            id, customer, driver, journey, status, date
        )
    }
}