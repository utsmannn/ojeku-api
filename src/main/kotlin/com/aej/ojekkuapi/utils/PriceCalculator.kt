package com.aej.ojekkuapi.utils

import com.aej.ojekkuapi.location.entity.Routes
import kotlin.math.ceil

object PriceCalculator {

    fun calculateRoute(routes: Routes, fixPrice: Double = 15000.0): Double {
        val km = routes.distance.toDouble() / 1000.0
        val originPrice = km * fixPrice
        return ceil(originPrice/1000) * 1000
    }
}