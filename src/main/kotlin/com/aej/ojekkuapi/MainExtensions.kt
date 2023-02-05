package com.aej.ojekkuapi

import com.aej.ojekkuapi.location.entity.Coordinate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.*

inline fun <reified T> T?.orThrow(
    message: String = "${T::class.simpleName} is null"
): T {
    return this ?: throw OjekuException(message)
}

inline fun <reified T> T?.toResult(
    message: String = "${T::class.simpleName} is null"
): Result<T> {
    return if (this != null) {
        Result.success(this)
    } else {
        Result.failure(OjekuException(message))
    }
}

fun <T> Result<T>.toResponses(): BaseResponse<T> {
    return if (this.isFailure) {
        throw OjekuException(this.exceptionOrNull()?.message ?: "Failure")
    } else {
        BaseResponse.success(this.getOrNull())
    }
}

fun String.coordinateStringToData(): Coordinate {
    val coordinateStrings = split(",")
    val lat = coordinateStrings[0].toDoubleOrNull() ?: 0.0
    val lon = coordinateStrings[1].toDoubleOrNull() ?: 0.0
    return Coordinate(lat, lon)
}

fun LocalDateTime.parseString(): String {
    val format = "dd LLLL yyyy, hh:mm a"
    val formatter = DateTimeFormatter.ofPattern(format)
    return this.format(formatter)
}

private fun distance(
    lat1: Double, lat2: Double, lon1: Double, lon2: Double
): Double {
    val earthRadius = 6371 // Radius of the earth
    val latDistance = Math.toRadians(lat2 - lat1)
    val lonDistance = Math.toRadians(lon2 - lon1)
    val a = (sin(latDistance / 2) * sin(latDistance / 2)
            + (cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2))
            * sin(lonDistance / 2) * sin(lonDistance / 2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    var distance = earthRadius * c * 1000 // convert to meters
    val height = 0.0
    distance = distance.pow(2.0) + height.pow(2.0)
    return sqrt(distance)
}

fun Coordinate.distanceTo(other: Coordinate): Double {
    val lat1 = this.latitude
    val lat2 = other.latitude
    val lon1 = this.longitude
    val lon2 = other.longitude

    return distance(lat1, lat2, lon1, lon2)
}