package com.aej.ojekkuapi.booking.services

import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.Reason
import com.aej.ojekkuapi.location.entity.Coordinate

interface BookingServices {

    suspend fun getCurrentBookingCustomer(customerId: String, status: Booking.BookingStatus): Result<Booking>
    suspend fun getBookingById(bookingId: String): Result<Booking>
    suspend fun getBookingsCustomer(customerId: String): Result<List<Booking>>
    suspend fun getBookingsCustomerByStatus(customerId: String, status: Booking.BookingStatus): Result<List<Booking>>
    suspend fun getBookingsDriverByStatus(driverId: String, status: Booking.BookingStatus): Result<List<Booking>>

    suspend fun getBookingsUserByStatus(userId: String, status: Booking.BookingStatus): Result<List<Booking>>
    suspend fun createBookingFromCustomer(customerId: String, from: Coordinate, destination: Coordinate): Result<Booking>
    suspend fun startBookingFromCustomer(bookingId: String, transType: Booking.TransType): Result<Booking>
    suspend fun cancelBookingFromCustomer(bookingId: String, reason: Reason): Result<Booking>
    suspend fun acceptBookingFromDriver(bookingId: String, driverId: String): Result<Booking>
    suspend fun rejectBookingFromDriver(bookingId: String, driverId: String): Result<Boolean>
    suspend fun ongoingBookingFromDriver(bookingId: String): Result<Booking>
    suspend fun completeBookingFromDriver(bookingId: String): Result<Booking>
    suspend fun getReasonList(): Result<List<Reason>>
}