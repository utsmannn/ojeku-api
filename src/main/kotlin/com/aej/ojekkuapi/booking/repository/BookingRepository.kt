package com.aej.ojekkuapi.booking.repository

import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.location.entity.Routes

interface BookingRepository {
    fun insertBooking(booking: Booking): Result<Booking>
    fun getBookingById(bookingId: String): Result<Booking>
    fun getBookingByCustomerId(customerId: String): Result<Booking>
    fun getBookingByDriverId(driverId: String): Result<Booking>
    fun getBookingsByCustomerId(customerId: String): Result<List<Booking>>
    fun getBookingByCustomerIdAndStatus(customerId: String, status: Booking.BookingStatus): Result<Booking>
    fun getBookingByDriverIdAndStatus(driverId: String, status: Booking.BookingStatus): Result<Booking>
    fun startBooking(bookingId: String, transType: Booking.TransType, driverId: String, price: Double): Result<Booking>
    fun updateBookingStatus(bookingId: String, bookingStatus: Booking.BookingStatus, driverId: String): Result<Booking>
}