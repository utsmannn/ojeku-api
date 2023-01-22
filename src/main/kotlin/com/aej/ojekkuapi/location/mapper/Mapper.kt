package com.aej.ojekkuapi.location.mapper

import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.BookingMinified
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.entity.Location
import com.aej.ojekkuapi.location.entity.LocationHereApiResult
import com.aej.ojekkuapi.location.entity.LocationHereRouteResult
import com.aej.ojekkuapi.location.util.PolylineEncoderDecoder

object Mapper {
    fun mapSearchLocationHereToLocation(locationResult: LocationHereApiResult): List<Location> {
        return locationResult.items?.map {
            val address = Location.Address(
                city = it?.address?.city.orEmpty(),
                country = it?.address?.countryName.orEmpty(),
                district = it?.address?.district.orEmpty()
            )
            Location(
                name = it?.title.orEmpty(),
                address = address,
                coordinate = Coordinate(it?.position?.lat ?: 0.0, it?.position?.lng ?: 0.0)
            )
        }.orEmpty()
    }

    fun mapRoutesHereToRoutes(locationResult: LocationHereRouteResult): Pair<List<Coordinate>, Long> {
        val section = locationResult.routes
            ?.firstOrNull()
            ?.sections
            ?.firstOrNull()

        val polylineString = section?.polyline.orEmpty()

        val coordinates = PolylineEncoderDecoder.decode(polylineString)
            .map { Coordinate(it.lat, it.lng) }
        val distance = section?.summary?.length ?: 0L
        return Pair(coordinates, distance)
    }

    fun mapBookingToMinified(booking: Booking): BookingMinified {
        return BookingMinified(
            id = booking.id,
            price = booking.price,
            transType = booking.transType,
            time = booking.time
        )
    }
}