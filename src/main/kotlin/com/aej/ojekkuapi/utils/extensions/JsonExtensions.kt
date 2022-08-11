package com.aej.ojekkuapi.utils.extensions

import com.fasterxml.jackson.databind.ObjectMapper

fun <T : Any, U : Any> U.safeCastTo(clazz: Class<T>): T {
    val json = ObjectMapper().writeValueAsString(this)
    return ObjectMapper().readValue(json, clazz)
}