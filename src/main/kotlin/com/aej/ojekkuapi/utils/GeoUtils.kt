package com.aej.ojekkuapi.utils

import com.aej.ojekkuapi.location.entity.Coordinate
import kotlin.math.*

object GeoUtils {
    private const val MAXITERS = 20

    // Porting from `android.location.computeDistanceAndBearing`
    fun computeDistance(
        coordinate1: Coordinate, coordinate2: Coordinate
    ): Double {
        // Based on http://www.ngs.noaa.gov/PUBS_LIB/inverse.pdf
        // using the "Inverse Formula" (section 4)
        var lat1 = coordinate1.latitude
        var lon1 = coordinate1.longitude
        var lat2 = coordinate2.latitude
        var lon2 = coordinate2.longitude
        // Convert lat/long to radians
        lat1 *= PI / 180.0
        lat2 *= PI / 180.0
        lon1 *= PI / 180.0
        lon2 *= PI / 180.0
        val a = 6378137.0 // WGS84 major axis
        val b = 6356752.3142 // WGS84 semi-major axis
        val f = (a - b) / a
        val aSqMinusBSqOverBSq = (a * a - b * b) / (b * b)
        val L = lon2 - lon1
        var A = 0.0
        val U1 = atan((1.0 - f) * tan(lat1))
        val U2 = atan((1.0 - f) * tan(lat2))
        val cosU1 = cos(U1)
        val cosU2 = cos(U2)
        val sinU1 = sin(U1)
        val sinU2 = sin(U2)
        val cosU1cosU2 = cosU1 * cosU2
        val sinU1sinU2 = sinU1 * sinU2
        var sigma = 0.0
        var deltaSigma = 0.0
        var cosSqAlpha: Double
        var cos2SM: Double
        var cosSigma: Double
        var sinSigma: Double
        var cosLambda: Double
        var sinLambda: Double
        var lambda = L // initial guess
        for (iter in 0 until MAXITERS) {
            val lambdaOrig = lambda
            cosLambda = cos(lambda)
            sinLambda = sin(lambda)
            val t1 = cosU2 * sinLambda
            val t2 = cosU1 * sinU2 - sinU1 * cosU2 * cosLambda
            val sinSqSigma = t1 * t1 + t2 * t2 // (14)
            sinSigma = sqrt(sinSqSigma)
            cosSigma = sinU1sinU2 + cosU1cosU2 * cosLambda // (15)
            sigma = atan2(sinSigma, cosSigma) // (16)
            val sinAlpha = if (sinSigma == 0.0) 0.0 else cosU1cosU2 * sinLambda / sinSigma // (17)
            cosSqAlpha = 1.0 - sinAlpha * sinAlpha
            cos2SM = if (cosSqAlpha == 0.0) 0.0 else cosSigma - 2.0 * sinU1sinU2 / cosSqAlpha // (18)
            val uSquared = cosSqAlpha * aSqMinusBSqOverBSq // defn
            A = 1 + uSquared / 16384.0 *  // (3)
                    (4096.0 + uSquared *
                            (-768 + uSquared * (320.0 - 175.0 * uSquared)))
            val B = uSquared / 1024.0 *  // (4)
                    (256.0 + uSquared *
                            (-128.0 + uSquared * (74.0 - 47.0 * uSquared)))
            val C = f / 16.0 *
                    cosSqAlpha *
                    (4.0 + f * (4.0 - 3.0 * cosSqAlpha)) // (10)
            val cos2SMSq = cos2SM * cos2SM
            deltaSigma = B * sinSigma *  // (6)
                    (cos2SM + B / 4.0 *
                            (cosSigma * (-1.0 + 2.0 * cos2SMSq) - B / 6.0 * cos2SM *
                                    (-3.0 + 4.0 * sinSigma * sinSigma) * (-3.0 + 4.0 * cos2SMSq)))
            lambda =
                L + (1.0 - C) * f * sinAlpha * (sigma + C * sinSigma * (cos2SM + C * cosSigma * (-1.0 + 2.0 * cos2SM * cos2SM))) // (11)
            val delta = (lambda - lambdaOrig) / lambda
            if (abs(delta) < 1.0e-12) {
                break
            }
        }
        return (b * A * (sigma - deltaSigma))
    }


    /**
     * For test distance
     * */
    /*@JvmStatic
    fun main(args: Array<String>) {
        val coor1 = Coordinate(-6.41204, 106.82477)
        val coor2 = Coordinate(-6.41213, 106.82474)
        val coor3 = Coordinate(-6.4123, 106.8247)
        val coor4 = Coordinate(-6.41242, 106.82467)
        val coor5 = Coordinate(-6.41266, 106.82458)
        val coor6 = Coordinate(-6.41278, 106.82453)
        val coor7 = Coordinate(-6.41302, 106.82443)
        val coor8 = Coordinate(-6.41316, 106.82436)
        val coor9 = Coordinate(-6.41327, 106.82427)
        val coor10 = Coordinate(-6.41352, 106.82408)

        val result = listOf(
            coor1, coor2, coor3, coor4, coor5, coor6, coor7, coor8, coor9, coor10
        ).computeDistance()

        val c1 = Coordinate(-6.412037, 106.824781)
        val c2 = Coordinate(-6.413516, 106.824079)

        val res1 = listOf(c1, c2).computeDistance()

        println("Distance -> $result | $res1")
    }*/
}