package com.aej.ojekkuapi.booking.controller

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.BookingMinified
import com.aej.ojekkuapi.booking.entity.Reason
import com.aej.ojekkuapi.booking.services.BookingServices
import com.aej.ojekkuapi.coordinateStringToData
import com.aej.ojekkuapi.location.mapper.Mapper
import com.aej.ojekkuapi.toResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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

    @GetMapping("/{booking_id}")
    suspend fun getBookingById(
        @PathVariable(value = "booking_id") bookingId: String
    ): BaseResponse<Booking> {
        return bookingServices.getBookingById(bookingId).toResponses()
    }

    @GetMapping("/activity")
    suspend fun getActivityBookingCustomer(): BaseResponse<List<BookingMinified>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return bookingServices.getBookingsCustomer(userId.orEmpty()).map {
            it.map { booking -> Mapper.mapBookingToMinified(booking) }
        }.toResponses()
    }

    @PostMapping("/customer/create")
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

    @PostMapping("/customer/request")
    suspend fun postBookingRequestCustomer(
        @RequestParam(value = "booking_id", required = true) bookingId: String,
        @RequestParam(value = "trans_type", required = true) transType: Booking.TransType
    ): BaseResponse<Booking> {
        return bookingServices.startBookingFromCustomer(bookingId, transType).toResponses()
    }

    @PostMapping("/customer/cancel")
    suspend fun cancelBookingCustomer(
        @RequestParam(value = "booking_id", required = true) bookingId: String,
        //@RequestParam(value = "reason_id", required = true) reasonId: String
    ): BaseResponse<Booking> {
        //val reason = Reason.reasonOf(reasonId)
        val reason = Reason.default
        return bookingServices.cancelBookingFromCustomer(bookingId, reason).toResponses()
    }

    @GetMapping("/customer/reason")
    suspend fun getReasonCustomer(): BaseResponse<List<Reason>> {
        return bookingServices.getReasonList().toResponses()
    }

    @PostMapping("/driver/reject")
    suspend fun rejectBookingDriver(
        @RequestParam(value = "booking_id", required = true) bookingId: String
    ): BaseResponse<Boolean> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return bookingServices.rejectBookingFromDriver(bookingId, userId.orEmpty()).toResponses()
    }

    @PostMapping("/driver/accept")
    suspend fun acceptBookingDriver(
        @RequestParam(value = "booking_id", required = true) bookingId: String
    ): BaseResponse<Booking> {
        val userId = SecurityContextHolder.getContext().authentication.principal as? String
        return bookingServices.acceptBookingFromDriver(bookingId, userId.orEmpty()).toResponses()
    }

    @PostMapping("/driver/take")
    suspend fun ongoingBookingDriver(
        @RequestParam(value = "booking_id", required = true) bookingId: String
    ): BaseResponse<Booking> {
        return bookingServices.ongoingBookingFromDriver(bookingId).toResponses()
    }

    @PostMapping("/driver/complete")
    suspend fun completeBookingDriver(
        @RequestParam(value = "booking_id", required = true) bookingId: String
    ): BaseResponse<Booking> {
        return bookingServices.completeBookingFromDriver(bookingId).toResponses()
    }


}