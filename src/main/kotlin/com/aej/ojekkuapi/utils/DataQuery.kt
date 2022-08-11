package com.aej.ojekkuapi.utils

import kotlin.reflect.KProperty1

data class DataQuery<D, T>(
    val property: KProperty1<D, T>,
    val value: T
)