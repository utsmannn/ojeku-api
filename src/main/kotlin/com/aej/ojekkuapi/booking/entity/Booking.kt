package com.aej.ojekkuapi.booking.entity

import com.aej.ojekkuapi.location.entity.Location
import com.aej.ojekkuapi.location.entity.Routes
import com.aej.ojekkuapi.parseString
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.time.LocalDateTime

data class Booking(
    @JsonProperty("id")
    var id: String = "",
    @JsonProperty("customer_id")
    var customerId: String = "",
    @JsonProperty("driver_id")
    var driverId: String = "",
    @JsonProperty("route_location")
    var routeLocation: RouteLocation = RouteLocation(),
    @JsonProperty("price")
    var price: Double = 0.0,
    @JsonProperty("status")
    var status: BookingStatus = BookingStatus.READY,
    @JsonProperty("trans_type")
    var transType: TransType = TransType.BIKE,
    @JsonProperty("time")
    var time: Time = Time()
) {
    data class RouteLocation(
        var from: Location = Location(),
        var destination: Location = Location(),
        var routes: Routes = Routes(0L, 0L, emptyList())
    )

    data class Time(
        @JsonProperty("local_date")
        val localDate: LocalDateTime = LocalDateTime.now(),
        val millis: Long = System.currentTimeMillis(),
        @JsonProperty("time_string")
        val timeString: String = LocalDateTime.now().parseString()
    )

    enum class BookingStatus {
        READY, REQUEST, REQUEST_RETRY, ACCEPTED, CANCELED, ONGOING, DONE, UNDEFINE
    }

    enum class TransType {
        BIKE, CAR
    }
}