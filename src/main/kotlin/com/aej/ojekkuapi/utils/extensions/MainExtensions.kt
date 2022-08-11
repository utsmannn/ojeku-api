package com.aej.ojekkuapi.utils.extensions

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.OjekuException
import com.aej.ojekkuapi.location.entity.Coordinate
import org.springframework.security.core.context.SecurityContextHolder

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

fun Any.findUserId(): String? = SecurityContextHolder.getContext().authentication.principal as? String