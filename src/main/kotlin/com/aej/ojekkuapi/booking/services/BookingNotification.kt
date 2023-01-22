package com.aej.ojekkuapi.booking.services

import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.messaging.MessagingComponent
import com.aej.ojekkuapi.messaging.entity.FcmMessage
import com.aej.ojekkuapi.user.services.UserServices
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

object BookingNotification {

    fun findDrivers(booking: Booking, userServices: UserServices, messagingComponent: MessagingComponent) {
        object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = Dispatchers.IO
        }.launch {
            val coordinate = booking.routeLocation.from.coordinate
            userServices.findDriverByCoordinate(coordinate)
                .getOrNull().orEmpty()
                .forEach { driver ->
                    val messageData = FcmMessage.FcmMessageData(
                        type = FcmMessage.Type.BOOKING,
                        customerId = booking.customerId,
                        driverId = "",
                        bookingId = booking.id
                    )

                    messagingComponent.sendMessage(driver.fcmToken, messageData)
                }
        }
    }
}