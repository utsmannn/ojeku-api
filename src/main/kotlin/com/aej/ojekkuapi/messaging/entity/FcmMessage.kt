package com.aej.ojekkuapi.messaging.entity

import com.fasterxml.jackson.annotation.JsonProperty

data class FcmMessage(
    @JsonProperty("to")
    val token: String,
    @JsonProperty("data")
    val data: FcmMessageData
) {

    data class FcmMessageData(
        @JsonProperty("type")
        val type: Type,
        @JsonProperty("customer_id")
        val customerId: String = "",
        @JsonProperty("driver_id")
        val driverId: String = "",
        @JsonProperty("booking_id")
        val bookingId: String = "",
        @JsonProperty("message")
        val message: String = "",
        @JsonProperty("json")
        val json: String = "{}"
    )

    enum class Type {
        GENERAL, BOOKING, BOOKING_REQUEST, BOOKING_UNAVAILABLE, BOOKING_LOCATION
    }
}