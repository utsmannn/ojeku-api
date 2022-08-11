package com.aej.ojekkuapi.transaction.entity

import com.aej.ojekkuapi.user.entity.User
import com.fasterxml.jackson.annotation.JsonProperty

data class TransactionDisplay(
    var id: String = "",
    @JsonProperty("customer_id")
    var customer: User.UserMini? = null,
    @JsonProperty("driver_id")
    var driver: User.UserMini? = null,
    var journey: Transaction.TransactionJourney = Transaction.TransactionJourney(),
    var status: Transaction.TransactionStatus = Transaction.TransactionStatus.INACTIVE,
    var date: Transaction.TransactionDate = Transaction.TransactionDate()
)