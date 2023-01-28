package com.aej.ojekkuapi.booking.repository

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.BookingDriver
import com.aej.ojekkuapi.toResult
import com.mongodb.client.MongoCollection
import org.litote.kmongo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class BookingDriverRepositoryImpl(
    @Autowired
    private val databaseComponent: DatabaseComponent
) : BookingDriverRepository {

    private fun getCollection(): MongoCollection<BookingDriver> {
        return databaseComponent.database.getDatabase("ojeku")
            .getCollection()
    }

    override fun createBookingDriver(bookingDriver: BookingDriver): Result<BookingDriver> {
        val existingBookingDriver = getBookingDriverByBookingId(bookingDriver.id)
        return if (existingBookingDriver.isSuccess) {
            existingBookingDriver
        } else {
            getCollection().insertOne(bookingDriver)
            getBookingDriverByBookingId(bookingDriver.id)
        }
    }

    override fun getBookingDriverByBookingId(bookingId: String): Result<BookingDriver> {
        return getCollection().findOne(BookingDriver::bookingId eq bookingId).toResult()
    }

    override fun removeDriverBooking(bookingId: String, driverId: String): Result<BookingDriver> {
        val currentBooking = getBookingDriverByBookingId(bookingId).getOrThrow()
        val updatedList = currentBooking.drivers.toMutableList()
        updatedList.remove(driverId)
        getCollection().updateOne(
            BookingDriver::bookingId eq bookingId,
            BookingDriver::drivers setTo updatedList
        ).toResult().getOrThrow()
        return getBookingDriverByBookingId(bookingId)
    }

    override fun clearBookingDriver(bookingId: String): Result<Boolean> {
        return getCollection().deleteOne(BookingDriver::bookingId eq bookingId).wasAcknowledged().toResult()
    }
}