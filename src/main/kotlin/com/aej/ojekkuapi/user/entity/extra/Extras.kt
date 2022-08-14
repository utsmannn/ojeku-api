package com.aej.ojekkuapi.user.entity.extra

open class Extras
fun emptyExtra() = object : Extras() {}

fun Extras.asDriverExtras(): DriverExtras = this as DriverExtras
fun Extras.asCustomerExtras(): CustomerExtras = this as CustomerExtras

fun Extras.isDriverExtras(): Boolean = this is DriverExtras
fun Extras.isCustomerExtras(): Boolean = this is CustomerExtras