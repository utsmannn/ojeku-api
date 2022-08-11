package com.aej.ojekkuapi.utils.extensions

import com.aej.ojekkuapi.utils.DataQuery
import org.bson.conversions.Bson
import org.litote.kmongo.combine
import org.litote.kmongo.setValue
import kotlin.reflect.KProperty1

infix fun <D, T>KProperty1<D, T>.to(value: T): DataQuery<D, T> {
    return DataQuery(this, value)
}

fun <D>Collection<DataQuery<D, *>>.combineUpdate(): Bson {
    return map {
        setValue(it.property, it.value)
    }.run {
        combine(this)
    }
}