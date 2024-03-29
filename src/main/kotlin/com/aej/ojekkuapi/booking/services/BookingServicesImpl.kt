package com.aej.ojekkuapi.booking.services

import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.BookingDriver
import com.aej.ojekkuapi.booking.entity.Reason
import com.aej.ojekkuapi.booking.repository.BookingDriverRepository
import com.aej.ojekkuapi.booking.repository.BookingRepository
import com.aej.ojekkuapi.distanceTo
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.services.LocationServices
import com.aej.ojekkuapi.messaging.MessagingComponent
import com.aej.ojekkuapi.messaging.entity.FcmMessage
import com.aej.ojekkuapi.toResult
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.repository.UserRepository
import com.aej.ojekkuapi.user.services.UserServices
import com.aej.ojekkuapi.utils.PriceCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import kotlin.coroutines.CoroutineContext

@Service
class BookingServicesImpl(
    @Autowired
    private val bookingRepository: BookingRepository,
    @Autowired
    private val bookingDriverRepository: BookingDriverRepository,
    @Autowired
    private val locationServices: LocationServices,
    @Autowired
    private val userServices: UserServices,
    @Autowired
    private val messagingComponent: MessagingComponent,
    @Autowired
    private val bookingNotification: BookingNotification
) : BookingServices {

    override suspend fun getCurrentBookingCustomer(customerId: String, status: Booking.BookingStatus): Result<Booking> {
        return bookingRepository.getBookingByCustomerIdAndStatus(customerId, status)
    }

    override suspend fun getBookingById(bookingId: String): Result<Booking> {
        return bookingRepository.getBookingById(bookingId)
    }

    override suspend fun getBookingsCustomer(customerId: String): Result<List<Booking>> {
        return bookingRepository.getBookingsByCustomerId(customerId)
    }

    override suspend fun getBookingsCustomerByStatus(
        customerId: String,
        status: Booking.BookingStatus
    ): Result<List<Booking>> {
        return bookingRepository.getBookingByCustomerIdAndStatusList(customerId, status)
    }

    override suspend fun getBookingsDriverByStatus(
        driverId: String,
        status: Booking.BookingStatus
    ): Result<List<Booking>> {
        return bookingRepository.getBookingByDriverIdAndStatusList(driverId, status)
    }

    override suspend fun getBookingsUserByStatus(userId: String, status: Booking.BookingStatus): Result<List<Booking>> {
        val user = userServices.getUserByUserId(userId)
        return when (user.getOrThrow().role) {
            User.Role.CUSTOMER -> getBookingsCustomerByStatus(userId, status)
            User.Role.DRIVER -> getBookingsDriverByStatus(userId, status)
        }
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

        val driverAvailable = bookingNotification.getDrivers(booking)
        val bookingDriver = BookingDriver(
            bookingId = booking.id,
            drivers = driverAvailable.map { it.id }
        )
        val existingBooking = bookingRepository.getBookingByCustomerId(customerId).getOrNull()
        return if (existingBooking != null) {
            val existingStatus = existingBooking.status
            val statusPermissionToBooking = listOf(
                Booking.BookingStatus.DONE,
                Booking.BookingStatus.CANCELED,
                Booking.BookingStatus.UNDEFINE
            )

            val isPermissionToBooking = statusPermissionToBooking.contains(existingStatus)
            if (isPermissionToBooking) {
                bookingDriverRepository.createBookingDriver(bookingDriver)
                bookingRepository.insertBooking(booking)
            } else {
                throw OjekuException("Booking already exists!")
            }
        } else {
            bookingDriverRepository.createBookingDriver(bookingDriver)
            bookingRepository.insertBooking(booking)
        }
    }

    override suspend fun startBookingFromCustomer(bookingId: String, transType: Booking.TransType): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        val currentRoutesLocation = currentBooking.routeLocation
        val currentRoutes = currentRoutesLocation.routes
        val newPrice = PriceCalculator.calculateRoute(currentRoutes)
        bookingRepository.startBooking(
            bookingId,
            transType,
            currentBooking.driverId,
            newPrice
        )

        val bookingDriver = bookingDriverRepository.getBookingDriverByBookingId(bookingId)
        val driverAvailable = bookingDriver.getOrThrow().drivers
        val drivers = bookingNotification.getDrivers(currentBooking)
            .filter { driverAvailable.contains(it.id) }
        if (drivers.isNotEmpty()) {
            val currentFirstDriver = drivers.first()
            notifyToUsers(currentBooking.customerId, currentFirstDriver.id, currentBooking, FcmMessage.Type.BOOKING_REQUEST)
        }
        return if (driverAvailable.isNotEmpty()) {
            bookingRepository.getBookingById(bookingId)
        } else {
            cancelBookingFromCustomer(bookingId, Reason.default)
        }
    }

    override suspend fun cancelBookingFromCustomer(bookingId: String, reason: Reason): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        bookingDriverRepository.clearBookingDriver(bookingId)
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

        bookingDriverRepository.clearBookingDriver(bookingId)
        bookingRepository.updateBookingStatus(
            bookingId,
            bookingStatus = Booking.BookingStatus.ACCEPTED,
            driverId = driverId
        )

        val updatedBooking = getBookingById(bookingId).getOrThrow()
        notifyToUsers(currentBooking.customerId, driverId, updatedBooking)
        notifyDriverPosition(driverId)
        return bookingRepository.getBookingById(bookingId)
    }

    override suspend fun rejectBookingFromDriver(bookingId: String, driverId: String): Result<Boolean> {
        val bookingDriver = bookingDriverRepository.removeDriverBooking(bookingId, driverId).getOrThrow()
        if (bookingDriver.drivers.isEmpty()) {
            cancelBookingFromCustomer(bookingId, Reason.default)
            return bookingDriverRepository.clearBookingDriver(bookingId)
        }

        val updatedBooking = bookingRepository.updateBookingStatus(
            bookingId = bookingId,
            bookingStatus = Booking.BookingStatus.REQUEST_RETRY,
            driverId = ""
        ).getOrThrow()

        notifyToUsers(updatedBooking.customerId, "", updatedBooking)
        return true.toResult()
    }

    override suspend fun ongoingBookingFromDriver(bookingId: String): Result<Booking> {
        val currentBooking = bookingRepository.getBookingById(bookingId).getOrThrow()
        val currentDriver = userServices.getUserByUserId(currentBooking.driverId).getOrThrow()

        val coordinateDriver = currentDriver.lastLocation
        val coordinatePickup = currentBooking.routeLocation.from.coordinate

        if (coordinateDriver.distanceTo(coordinatePickup) > 50.0) {
            throw OjekuException("Driver is too far")
        }

        bookingRepository.updateBookingStatus(
            bookingId,
            bookingStatus = Booking.BookingStatus.ONGOING,
            driverId = currentBooking.driverId
        )
        notifyToUsers(currentBooking.customerId, currentBooking.driverId, currentBooking)
        notifyDriverPosition(currentBooking.driverId)
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

    override suspend fun getReasonList(): Result<List<Reason>> {
        return Result.success(
            Reason.list
        )
    }

    private suspend fun notifyToUsers(
        customerId: String,
        driverId: String,
        booking: Booking,
        type: FcmMessage.Type = FcmMessage.Type.BOOKING
    ) {
        val customer = userServices.getUserByUserId(customerId).getOrNull()
        val driver = userServices.getUserByUserId(driverId).getOrNull()

        val messageData = FcmMessage.FcmMessageData(
            type = type,
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

    private fun notifyDriverPosition(driverId: String) {
        object : CoroutineScope {
            override val coroutineContext: CoroutineContext
                get() = Dispatchers.IO
        }.launch {
            delay(3000)
            val currentDriver = userServices.getUserByUserId(driverId).getOrThrow()
            val driverCoordinate = currentDriver.lastLocation
            userServices.updateUserLocation(driverId, driverCoordinate)
        }
    }
}