package com.aej.ojekkuapi.location.mapper

import com.aej.ojekkuapi.booking.entity.Booking
import com.aej.ojekkuapi.booking.entity.BookingMinified
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.entity.Location
import com.aej.ojekkuapi.location.entity.LocationHereApiResult
import com.aej.ojekkuapi.location.entity.LocationHereRouteResult
import com.aej.ojekkuapi.location.entity.response.OpenRouteResponse
import com.aej.ojekkuapi.location.util.PolylineEncoderDecoder
import kotlin.math.ceil

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

    fun mapRoutesHereToRoutes(locationResult: LocationHereRouteResult): Triple<List<Coordinate>, Long, Long> {
        val section = locationResult.routes
            ?.firstOrNull()
            ?.sections
            ?.firstOrNull()

        val polylineString = section?.polyline.orEmpty()

        val coordinates = PolylineEncoderDecoder.decode(polylineString)
            .map { Coordinate(it.lat, it.lng) }
        val distance = section?.summary?.length ?: 0L
        val duration = section?.summary?.duration ?: 0L
        return Triple(coordinates, distance, duration)
    }

    fun mapOpenRouteToRoutes(routeResponse: OpenRouteResponse): Pair<List<Coordinate>, Long> {
        val feature = routeResponse.features?.firstOrNull()
        val length = feature?.properties?.summary?.distance?.toLong() ?: 0L
        val coordinates = feature?.geometry
            ?.coordinates
            .orEmpty()
            .map {
                val lat = it?.get(1) ?: 0.0
                val lon = it?.get(0) ?: 0.0
                Coordinate(lat, lon)
            }
        return Pair(coordinates, length)
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