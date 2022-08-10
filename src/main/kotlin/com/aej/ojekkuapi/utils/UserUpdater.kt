package com.aej.ojekkuapi.utils

import com.aej.ojekkuapi.user.entity.User
import kotlin.reflect.KProperty1

data class UserUpdater<T>(
    val property: KProperty1<User, T>,
    val value: T
)