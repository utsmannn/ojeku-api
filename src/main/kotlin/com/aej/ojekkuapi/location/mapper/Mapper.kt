package com.aej.ojekkuapi.location.mapper

import com.aej.ojekkuapi.location.entity.*
import com.aej.ojekkuapi.location.util.PolylineEncoderDecoder

object Mapper {
    fun mapSearchLocationHereToLocation(locationResult: LocationHereApiResult): List<Location> {
        return locationResult.items?.map {
            val address = Location.Address(
                city = it?.address?.city.orEmpty(),
                country = it?.address?.countryName.orEmpty(),
                distric = it?.address?.district.orEmpty()
            )
            Location(
                name = it?.title.orEmpty(),
                address = address,
                coordinate = Coordinate(it?.position?.lat ?: 0.0, it?.position?.lng ?: 0.0)
            )
        }.orEmpty()
    }

    fun mapRoutesHereToRoutes(locationResult: LocationHereRouteResult): Routes {
        val section = locationResult.routes
            ?.firstOrNull()
            ?.sections
            ?.firstOrNull()

        val polylineString = section?.polyline
            .orEmpty()

        val distanceInMeter = section?.summary?.length ?: 0
        val polyline = PolylineEncoderDecoder.decode(polylineString)
            .map { Coordinate(it.lat, it.lng) }
        return Routes(distanceInMeter.toDouble(), polyline)
    }
}