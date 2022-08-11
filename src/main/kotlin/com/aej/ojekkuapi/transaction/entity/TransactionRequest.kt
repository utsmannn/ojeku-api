package com.aej.ojekkuapi.transaction.entity

import com.aej.ojekkuapi.location.entity.Coordinate

data class TransactionRequest(
    val origin: Coordinate,
    val destination: Coordinate
)