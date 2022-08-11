package com.aej.ojekkuapi.location.mapper

import com.aej.ojekkuapi.location.entity.*
import com.aej.ojekkuapi.location.util.PolylineEncoderDecoder
import com.aej.ojekkuapi.utils.extensions.computeDistance

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

        val polyline = PolylineEncoderDecoder.decode(polylineString)
            .map { Coordinate(it.lat, it.lng) }
        val distanceInMeter = polyline.computeDistance()
        return Routes(distanceInMeter, polyline)
    }
}