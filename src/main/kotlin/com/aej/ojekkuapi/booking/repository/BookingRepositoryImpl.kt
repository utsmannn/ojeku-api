package com.aej.ojekkuapi.booking.repository

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.location.entity.Routes
import com.aej.ojekkuapi.toResult
import com.mongodb.client.MongoCollection
import org.litote.kmongo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class BookingRepositoryImpl(
    @Autowired
    private val databaseComponent: DatabaseComponent
) : BookingRepository {

    private fun getCollection(): MongoCollection<Booking> {
        return databaseComponent.database.getDatabase("ojeku")
            .getCollection()
    }

    override fun insertBooking(booking: Booking): Result<Booking> {
        val existingReadyBook =
            getBookingByCustomerIdAndStatus(booking.customerId, Booking.BookingStatus.READY).getOrNull()

        return if (existingReadyBook != null) {
            throw OjekuException("Transaction is exists")
        } else {
            getCollection().insertOne(booking)
            getBookingById(booking.id)
        }
    }

    override fun getBookingById(bookingId: String): Result<Booking> {
        return getCollection().findOne(Booking::id eq bookingId).toResult("Transaction not found")
    }

    override fun getBookingsByCustomerId(customerId: String): Result<List<Booking>> {
        return getCollection().find(Booking::customerId eq customerId).toList().toResult("Transaction not found")
    }

    override fun getBookingByCustomerIdAndStatus(customerId: String, status: Booking.BookingStatus): Result<Booking> {
        return getCollection().findOne(
            Booking::customerId eq customerId,
            Booking::status eq status
        ).toResult("Transaction not found")
    }


    override fun getBookingByCustomerIdAndStatusList(
        customerId: String,
        status: Booking.BookingStatus
    ): Result<List<Booking>> {
        return getCollection().find(
            Booking::customerId eq customerId,
            Booking::status eq status
        ).sort(
            descending(
                Booking::time
            )
        ).toList().toResult("Transaction not found")
    }

    override fun getBookingByDriverIdAndStatusList(
        driverId: String,
        status: Booking.BookingStatus
    ): Result<List<Booking>> {
        return getCollection().find(
            Booking::driverId eq driverId,
            Booking::status eq status
        ).sort(
            descending(
                Booking::time
            )
        ).toList().toResult("Transaction not found")
    }

    override fun getBookingByDriverIdAndStatus(driverId: String, status: Booking.BookingStatus): Result<Booking> {
        return getCollection().findOne(
            Booking::driverId eq driverId,
            Booking::status eq status
        ).toResult("Transaction not found")
    }

    override fun getBookingByCustomerId(customerId: String): Result<Booking> {
        return getCollection().findOne(Booking::customerId eq customerId).toResult("Transaction not found!")
    }

    override fun getBookingByDriverId(driverId: String): Result<Booking> {
        return getCollection().findOne(Booking::driverId eq driverId).toResult("Transaction not found!")
    }

    override fun startBooking(
        bookingId: String,
        transType: Booking.TransType,
        driverId: String,
        price: Double
    ): Result<Booking> {
        getCollection().updateOne(
            Booking::id eq bookingId,
            Booking::transType setTo transType,
            Booking::driverId setTo driverId,
            Booking::price setTo price,
            Booking::status setTo Booking.BookingStatus.REQUEST,
        ).toResult()
        return getBookingById(bookingId)
    }

    override fun updateBookingStatus(
        bookingId: String,
        bookingStatus: Booking.BookingStatus,
        driverId: String
    ): Result<Booking> {
        getCollection().updateOne(
            Booking::id eq bookingId,
            Booking::status setTo bookingStatus,
            Booking::driverId setTo driverId
        ).toResult()
        return getBookingById(bookingId)
    }
}