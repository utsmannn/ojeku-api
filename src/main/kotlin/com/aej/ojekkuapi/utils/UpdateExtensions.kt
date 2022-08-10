package com.aej.ojekkuapi.utils

import com.aej.ojekkuapi.user.entity.User
import kotlin.reflect.KProperty1

fun <T>KProperty1<User, T>.update(value: T): UserUpdater<T> {
    return UserUpdater(this, value)
}