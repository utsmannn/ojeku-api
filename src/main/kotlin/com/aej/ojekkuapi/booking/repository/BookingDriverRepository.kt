package com.aej.ojekkuapi.booking.repository

import com.aej.ojekkuapi.booking.entity.BookingDriver

interface BookingDriverRepository {

    fun createBookingDriver(bookingDriver: BookingDriver): Result<BookingDriver>
    fun getBookingDriverByBookingId(bookingId: String): Result<BookingDriver>
    fun removeDriverBooking(bookingId: String, driverId: String): Result<BookingDriver>
    fun clearBookingDriver(bookingId: String): Result<Boolean>
}