package com.aej.ojekkuapi.booking.controller

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.BookingMinified
import com.aej.ojekkuapi.booking.services.BookingServices
import com.aej.ojekkuapi.coordinateStringToData
import com.aej.ojekkuapi.location.mapper.Mapper
import com.aej.ojekkuapi.toResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/booking")
class BookingController {

    @Autowired
    private lateinit var bookingServices: BookingServices

    @GetMapping
    suspend fun getCurrentBookingCustomer(
        @RequestParam(value = "status") status: Booking.BookingStatus
    ): BaseResponse<Booking> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return bookingServices.getCurrentBookingCustomer(userId.orEmpty(), status).toResponses()
    }

    @GetMapping("/activity")
    suspend fun getActivityBookingCustomer(): BaseResponse<List<BookingMinified>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return bookingServices.getBookingsCustomer(userId.orEmpty()).map {
            it.map { booking -> Mapper.mapBookingToMinified(booking) }
        }.toResponses()
    }

    @PostMapping
    suspend fun postBookingCustomer(
        @RequestParam(value = "from", required = true) from: String,
        @RequestParam(value = "destination", required = true) destination: String
    ): BaseResponse<Booking> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        val coordinateFrom = from.coordinateStringToData()
        val coordinateDestination = destination.coordinateStringToData()

        return bookingServices.createBookingFromCustomer(userId.orEmpty(), coordinateFrom, coordinateDestination)
            .toResponses()
    }

    @PostMapping("/request")
    suspend fun postBookingRequestCustomer(
        @RequestParam(value = "booking_id", required = true) bookingId: String,
        @RequestParam(value = "trans_type", required = true) transType: Booking.TransType
    ): BaseResponse<Booking> {
        return bookingServices.startBookingFromCustomer(bookingId, transType).toResponses()
    }

    @PostMapping("/cancel")
    suspend fun cancelBookingCustomer(
        @RequestParam(value = "booking_id", required = true) bookingId: String
    ): BaseResponse<Booking> {
        return bookingServices.cancelBookingFromCustomer(bookingId).toResponses()
    }
}