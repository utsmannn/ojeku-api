package com.aej.ojekkuapi.booking.entity

import com.aej.ojekkuapi.OjekuException
import java.util.UUID

private enum class ReasonEnum(val value: String) {
    DRIVER_FAR_AWAY("Driver is too far away"),
    DRIVER_UN_CONTACTABLE("Driver is un-contactable"),
    CHANGE_MY_MIND("I changed my mind"),
    CHANGE_TRANSPORT("I want to change transportation"),
    CHANGE_DESTINATION("Want to change destination"),
    WAITED_LONG("I waited too long"),
    OTHER("Other")
}


data class Reason(
    val id: String,
    val name: String
) {

    companion object {
        val list: List<Reason> = ReasonEnum.values().map {
            Reason(id = it.name, name = it.value)
        }

        val default: Reason = Reason(ReasonEnum.OTHER.name, ReasonEnum.OTHER.value)

        fun reasonOf(id: String): Reason {
            val enum = ReasonEnum.values().find { it.name == id } ?: throw OjekuException("Unknown reason")
            return Reason(enum.name, enum.value)
        }
    }
}