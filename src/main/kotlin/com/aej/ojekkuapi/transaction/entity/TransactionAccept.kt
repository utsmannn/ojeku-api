package com.aej.ojekkuapi.transaction.entity

data class TransactionAccept(
    var isAccepted: Boolean = false,
    var transaction: Transaction? = null
)