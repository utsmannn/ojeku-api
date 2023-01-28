package com.aej.ojekkuapi.booking.services

import com.aej.ojekkuapi.booking.entity.BookingDriver
import com.aej.ojekkuapi.booking.repository.BookingDriverRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class BookingDriverServicesImpl(
    @Autowired
    private val bookingDriverRepository: BookingDriverRepository
) : BookingDriverServices {
    override suspend fun createBookingDriver(bookingDriver: BookingDriver): Result<BookingDriver> {
        return bookingDriverRepository.createBookingDriver(bookingDriver)
    }

    override suspend fun continueBookingDriver(bookingId: String, exceptDriverId: String): Result<BookingDriver> {
        return bookingDriverRepository.removeDriverBooking(bookingId, exceptDriverId)
    }

    override suspend fun deleteBookingDriver(bookingId: String): Result<Boolean> {
        return bookingDriverRepository.clearBookingDriver(bookingId)
    }


}