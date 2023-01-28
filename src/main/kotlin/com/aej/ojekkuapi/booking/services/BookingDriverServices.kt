package com.aej.ojekkuapi.booking.services

import com.aej.ojekkuapi.booking.entity.BookingDriver

interface BookingDriverServices {
    suspend fun createBookingDriver(bookingDriver: BookingDriver): Result<BookingDriver>
    suspend fun continueBookingDriver(bookingId: String, exceptDriverId: String): Result<BookingDriver>
    suspend fun deleteBookingDriver(bookingId: String): Result<Boolean>
}