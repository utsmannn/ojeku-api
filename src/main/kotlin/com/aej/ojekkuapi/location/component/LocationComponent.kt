package com.aej.ojekkuapi.location.component

import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.entity.LocationHereApiResult
import com.aej.ojekkuapi.location.entity.LocationHereRouteResult
import com.aej.ojekkuapi.location.entity.Routes
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import okhttp3.OkHttpClient
import okhttp3.Request
import org.springframework.stereotype.Component

@Component
class LocationComponent {


    private val client = OkHttpClient()

    private inline fun <reified T> getHttp(url: String): Result<T> {
        return try {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val body = response.body
            val bodyString = body?.string()
            if (response.isSuccessful) {
                val data = ObjectMapper().readValue(bodyString, T::class.java)
                Result.success(data)
            } else {
                val throwable = IllegalArgumentException(response.message)
                Result.failure(throwable)
            }
        } catch (e: JsonParseException) {
            Result.failure(e)
        } catch (e: InvalidDefinitionException) {
            Result.failure(e)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

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

        return getHttp(url)
    }

    companion object {
        private const val API_KEY = "-pCyFg9ZHjs7E0Oz4KPBU_EuMdLUODHUAJ7qPLb6v70"

        const val SEARCH_LOC = "https://discover.search.hereapi.com/v1/discover?" +
                "at={{coordinate}}" +
                "&limit=21" +
                "&q={{name}}" +
                "&apiKey=$API_KEY"
        const val RESERVE_LOC = "https://revgeocode.search.hereapi.com/v1/revgeocode?" +
                "at={{coordinate}}" +
                "&lang=en-US" +
                "&apiKey=$API_KEY"
        const val ROUTES_POLYLINE_LOC = "https://router.hereapi.com/v8/routes?" +
                "transportMode=scooter" +
                "&origin={{coordinate_origin}}" +
                "&destination={{coordinate_destination}}" +
                "&return=polyline,summary" +
                "&apikey=$API_KEY"
    }

    object Key {
        const val COORDINATE = "{{coordinate}}"
        const val NAME = "{{name}}"

        const val COORDINATE_ORIGIN = "{{coordinate_origin}}"
        const val COORDINATE_DESTINATION = "{{coordinate_destination}}"
    }

}