package com.aej.ojekkuapi.booking.entity

import java.util.UUID

data class BookingDriver(
    val id: String = UUID.randomUUID().toString(),
    val bookingId: String,
    val drivers: List<String>
)