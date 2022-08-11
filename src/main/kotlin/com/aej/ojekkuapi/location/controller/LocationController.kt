package com.aej.ojekkuapi.location.controller

import com.aej.ojekkuapi.BaseResponse
import com.aej.ojekkuapi.utils.extensions.coordinateStringToData
import com.aej.ojekkuapi.location.entity.Location
import com.aej.ojekkuapi.location.entity.Routes
import com.aej.ojekkuapi.location.services.LocationServices
import com.aej.ojekkuapi.utils.extensions.toResponses
import com.aej.ojekkuapi.user.entity.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/location")
class LocationController {

    @Autowired
    private lateinit var locationServices: LocationServices

    @GetMapping("/search")
    fun searchLocation(
        @RequestParam(value = "name", required = true) name: String,
        @RequestParam(value = "coordinate", required = true) coordinate: String
    ): BaseResponse<List<Location>> {
        val coordinateData = coordinate.coordinateStringToData()
        return locationServices.searchLocation(name, coordinateData).toResponses()
    }

    @GetMapping("/reserve")
    fun reserveLocation(
        @RequestParam(value = "coordinate", required = true) coordinate: String
    ): BaseResponse<Location> {
        val coordinateData = coordinate.coordinateStringToData()
        return locationServices.reserveLocation(coordinateData).toResponses()
    }

    @GetMapping("/routes")
    fun routesLocation(
        @RequestParam(value = "origin") origin: String,
        @RequestParam(value = "destination") destination: String
    ): BaseResponse<Routes> {
        val coordinateOrigin = origin.coordinateStringToData()
        val coordinateDestination = destination.coordinateStringToData()
        return locationServices.getRoutesLocation(coordinateOrigin, coordinateDestination).toResponses()
    }

    @GetMapping("/distance")
    fun calculateDistance(
        @RequestParam(value = "origin") origin: String,
        @RequestParam(value = "destination") destination: String
    ): BaseResponse<Double> {
        val coordinateOrigin = origin.coordinateStringToData()
        val coordinateDestination = destination.coordinateStringToData()
        return locationServices.calculateDistance(coordinateOrigin, coordinateDestination).toResponses()
    }

    @GetMapping("/find_driver")
    fun findDriver(
        @RequestParam(value = "coordinate") coordinateString: String
    ): BaseResponse<List<User>> {
        val coordinate = coordinateString.coordinateStringToData()
        return locationServices.findDriverFromCoordinate(coordinate).toResponses()
    }
}