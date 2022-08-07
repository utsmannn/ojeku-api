package com.aej.ojekkuapi

import com.aej.ojekkuapi.location.entity.Coordinate

import com.aej.ojekkuapi.user.entity.User

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

fun <T>Result<T>.toResponses(): BaseResponse<T> {
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