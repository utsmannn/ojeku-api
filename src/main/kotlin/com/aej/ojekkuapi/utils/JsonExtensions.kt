package com.aej.ojekkuapi.utils

import com.aej.ojekkuapi.user.entity.extra.Extras
import com.fasterxml.jackson.databind.ObjectMapper
import org.litote.kmongo.json

fun <T: Any, U: Any>T.safeCastTo(clazz: Class<U>): U {
    val json = ObjectMapper().writeValueAsString(this)
    return ObjectMapper().readValue(json, clazz)
}

fun <T: Any>T.toJson(): String {
    return json
}