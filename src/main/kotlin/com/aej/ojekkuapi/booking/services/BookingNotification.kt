package com.aej.ojekkuapi.booking.services

import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.BookingDriver
import com.aej.ojekkuapi.booking.repository.BookingDriverRepository
import com.aej.ojekkuapi.booking.repository.BookingRepository
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.messaging.MessagingComponent
import com.aej.ojekkuapi.messaging.entity.FcmMessage
import com.aej.ojekkuapi.user.entity.request.UserView
import com.aej.ojekkuapi.user.services.UserServices
import kotlinx.coroutines.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.coroutines.CoroutineContext

@Component
class BookingNotification(
    @Autowired
    private val messagingComponent: MessagingComponent,
    @Autowired
    private val userServices: UserServices,
    @Autowired
    private val bookingDriverRepository: BookingDriverRepository
) {

    suspend fun getDrivers(booking: Booking): List<UserView> {
        return object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = Dispatchers.IO
        }.async {
            val coordinate = booking.routeLocation.from.coordinate
            val drivers = userServices.findDriverByCoordinate(coordinate)
                .getOrNull().orEmpty()

            drivers
        }.await()
    }

    suspend fun findDrivers(booking: Booking, exceptDriverId: String) : Boolean {
        return object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = Dispatchers.IO
        }.async {
            val coordinate = booking.routeLocation.from.coordinate
            val drivers = userServices.findDriverByCoordinate(coordinate)
                .getOrNull().orEmpty()
                .filter { it.id != exceptDriverId }

            if (drivers.isNotEmpty()) {
                val bookingDriver = BookingDriver(
                    bookingId = booking.id,
                    drivers = drivers.map { it.id }
                )
                bookingDriverRepository.createBookingDriver(bookingDriver)
                drivers.forEach { driver ->
                    val messageData = FcmMessage.FcmMessageData(
                        type = FcmMessage.Type.BOOKING,
                        customerId = booking.customerId,
                        driverId = "",
                        bookingId = booking.id,
                        message = "Offering incoming"
                    )

                    messagingComponent.sendMessage(driver.fcmToken, messageData)
                }
            } else {
                val messageData = FcmMessage.FcmMessageData(
                    type = FcmMessage.Type.BOOKING_UNAVAILABLE,
                    customerId = booking.customerId,
                    driverId = "",
                    bookingId = booking.id,
                    message = "No driver available!"
                )
                val currentUser = userServices.getUserByUserId(booking.customerId)
                messagingComponent.sendMessage(currentUser.getOrThrow().fcmToken, messageData)
            }

            drivers.isNotEmpty()
        }.await()
    }
}