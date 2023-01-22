package com.aej.ojekkuapi.utils

import com.aej.ojekkuapi.location.entity.Routes

object PriceCalculator {

    fun calculateRoute(routes: Routes, fixPrice: Double = 15000.0): Double {
        val km = routes.distance.toDouble() / 1000.0
        return km * fixPrice
    }
}