package com.aej.ojekkuapi.booking.services

import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.repository.BookingRepository
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.services.LocationServices
import com.aej.ojekkuapi.messaging.MessagingComponent
import com.aej.ojekkuapi.messaging.entity.FcmMessage
import com.aej.ojekkuapi.user.repository.UserRepository
import com.aej.ojekkuapi.user.services.UserServices
import com.aej.ojekkuapi.utils.PriceCalculator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class BookingServicesImpl(
    @Autowired
    private val bookingRepository: BookingRepository,
    @Autowired
    private val locationServices: LocationServices,
    @Autowired
    private val userServices: UserServices,
    @Autowired
    private val messagingComponent: MessagingComponent
) : BookingServices {

    override suspend fun getCurrentBookingCustomer(customerId: String, status: Booking.BookingStatus): Result<Booking> {
        return bookingRepository.getBookingByCustomerIdAndStatus(customerId, status)
    }

    override suspend fun getBookingsCustomer(customerId: String): Result<List<Booking>> {
        return bookingRepository.getBookingsByCustomerId(customerId)
    }

    override suspend fun createBookingFromCustomer(
        customerId: String,
        from: Coordinate,
        destination: Coordinate
    ): Result<Booking> {
        if (customerId.isEmpty()) throw OjekuException("Customer id is empty!")

        val routes = locationServices.getRoutesLocation(from, destination).getOrThrow()
        val fromLocation = locationServices.reserveLocation(from).getOrThrow()
        val destLocation = locationServices.reserveLocation(destination).getOrThrow()
        val price = PriceCalculator.calculateRoute(routes)

        val routeLocation = Booking.RouteLocation(
            from = fromLocation,
            destination = destLocation,
            routes = routes
        )

        val booking = Booking(
            id = UUID.randomUUID().toString(),
            customerId = customerId,
            routeLocation = routeLocation,
            price = price,
            status = Booking.BookingStatus.READY
        )

        return bookingRepository.insertBooking(booking)
    }

    override suspend fun startBookingFromCustomer(bookingId: String, transType: Booking.TransType): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        val currentRoutesLocation = currentBooking.routeLocation
        val currentRoutes = currentRoutesLocation.routes
        val newPrice = PriceCalculator.calculateRoute(currentRoutes, 22000.0)
        bookingRepository.startBooking(
            bookingId,
            transType,
            currentBooking.driverId,
            newPrice
        )

        BookingNotification.findDrivers(
            booking = currentBooking,
            userServices = userServices,
            messagingComponent = messagingComponent
        )

        notifyToUsers(currentBooking.customerId, currentBooking.driverId, currentBooking)
        return bookingRepository.getBookingById(bookingId)
    }

    override suspend fun cancelBookingFromCustomer(bookingId: String): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        bookingRepository.updateBookingStatus(
            bookingId,
            bookingStatus = Booking.BookingStatus.CANCELED,
            driverId = currentBooking.driverId
        )
        notifyToUsers(currentBooking.customerId, currentBooking.driverId, currentBooking)
        return bookingRepository.getBookingById(bookingId)
    }

    override suspend fun acceptBookingFromDriver(bookingId: String, driverId: String): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        bookingRepository.updateBookingStatus(
            bookingId,
            bookingStatus = Booking.BookingStatus.ACCEPTED,
            driverId = currentBooking.driverId
        )
        notifyToUsers(currentBooking.customerId, currentBooking.driverId, currentBooking)
        return bookingRepository.getBookingById(bookingId)
    }

    override suspend fun ongoingBookingFromDriver(bookingId: String): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        bookingRepository.updateBookingStatus(
            bookingId,
            bookingStatus = Booking.BookingStatus.ONGOING,
            driverId = currentBooking.driverId
        )
        notifyToUsers(currentBooking.customerId, currentBooking.driverId, currentBooking)
        return bookingRepository.getBookingById(bookingId)
    }

    override suspend fun completeBookingFromDriver(bookingId: String): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        bookingRepository.updateBookingStatus(
            bookingId,
            bookingStatus = Booking.BookingStatus.DONE,
            driverId = currentBooking.driverId
        )
        notifyToUsers(currentBooking.customerId, currentBooking.driverId, currentBooking)
        return bookingRepository.getBookingById(bookingId)
    }

    private suspend fun notifyToUsers(customerId: String, driverId: String, booking: Booking) {
        val customer = userServices.getUserByUserId(customerId).getOrNull()
        val driver = userServices.getUserByUserId(driverId).getOrNull()

        val messageData = FcmMessage.FcmMessageData(
            type = FcmMessage.Type.BOOKING,
            customerId = customerId,
            driverId = driverId,
            bookingId = booking.id
        )

        if (customer != null) {
            messagingComponent.sendMessage(customer.fcmToken, messageData)
        }

        if (driver != null) {
            messagingComponent.sendMessage(driver.fcmToken, messageData)
        }
    }
}