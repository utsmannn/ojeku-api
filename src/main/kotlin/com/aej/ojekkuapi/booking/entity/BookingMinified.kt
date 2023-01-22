package com.aej.ojekkuapi.booking.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class BookingMinified(
    val id: String,
    val price: Double,
    @JsonProperty("trans_type")
    val transType: Booking.TransType,
    val time: Booking.Time
)