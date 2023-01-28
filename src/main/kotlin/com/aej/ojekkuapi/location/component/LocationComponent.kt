package com.aej.ojekkuapi.location.component

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.entity.LocationHereApiResult
import com.aej.ojekkuapi.location.entity.LocationHereRouteResult
import com.aej.ojekkuapi.utils.BaseComponent
import org.springframework.stereotype.Component

@Component
class LocationComponent : BaseComponent() {

    fun searchLocation(name: String, coordinate: Coordinate): Result<LocationHereApiResult> {
        val coordinateString = "${coordinate.latitude},${coordinate.longitude}"
        val url = SEARCH_LOC
            .replace(Key.COORDINATE, coordinateString)
            .replace(Key.NAME, name)
        return getHttp(url)
    }

    fun reserveLocation(coordinate: Coordinate): Result<LocationHereApiResult> {
        val coordinateString = "${coordinate.latitude},${coordinate.longitude}"
        val url = RESERVE_LOC
            .replace(Key.COORDINATE, coordinateString)

        return getHttp(url)
    }

    fun getRoutes(origin: Coordinate, destination: Coordinate): Result<LocationHereRouteResult> {
        val coordinateOriginString = "${origin.latitude},${origin.longitude}"
        val coordinateDestinationString = "${destination.latitude},${destination.longitude}"
        val url = ROUTES_POLYLINE_LOC
            .replace(Key.COORDINATE_ORIGIN, coordinateOriginString)
            .replace(Key.COORDINATE_DESTINATION, coordinateDestinationString)
            .replace(Key.TRANSPORT_TYPE, "car")

        return getHttp(url)
    }

    companion object {
        private const val HERE_API_KEY = "5R7iofrUbgpXBsRjTvqQLfm9v8nYqMQnTIXY3xjBHUY"
        const val SEARCH_LOC = "https://discover.search.hereapi.com/v1/discover?at={{coordinate}}&limit=21&q={{name}}&apiKey=$HERE_API_KEY"
        const val RESERVE_LOC = "https://revgeocode.search.hereapi.com/v1/revgeocode?at={{coordinate}}&lang=en-US&apiKey=$HERE_API_KEY"
        const val ROUTES_POLYLINE_LOC = "https://router.hereapi.com/v8/routes?transportMode={{transport_type}}&origin={{coordinate_origin}}&destination={{coordinate_destination}}&return=polyline,summary&apikey=$HERE_API_KEY"
    }

    object Key {
        const val COORDINATE = "{{coordinate}}"
        const val NAME = "{{name}}"

        const val COORDINATE_ORIGIN = "{{coordinate_origin}}"
        const val COORDINATE_DESTINATION = "{{coordinate_destination}}"

        const val TRANSPORT_TYPE = "{{transport_type}}"
    }

}