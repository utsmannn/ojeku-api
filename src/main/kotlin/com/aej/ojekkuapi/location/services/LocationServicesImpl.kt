package com.aej.ojekkuapi.location.services

import com.aej.ojekkuapi.location.component.LocationComponent
import com.aej.ojekkuapi.location.mapper.Mapper
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.location.entity.Location
import com.aej.ojekkuapi.location.entity.Routes
import com.aej.ojekkuapi.utils.extensions.orThrow
import com.aej.ojekkuapi.user.entity.User
import com.aej.ojekkuapi.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LocationServicesImpl(
    @Autowired
    private val fetcher: LocationComponent,
    @Autowired
    private val userRepository: UserRepository
) : LocationServices {

    override fun searchLocation(name: String, coordinate: Coordinate): Result<List<Location>> {
        return fetcher.searchLocation(name, coordinate).map {
            Mapper.mapSearchLocationHereToLocation(it)
        }
    }

    override fun reserveLocation(coordinate: Coordinate): Result<Location> {
        return fetcher.reserveLocation(coordinate).map {
            Mapper.mapSearchLocationHereToLocation(it).firstOrNull().orThrow("Location not found!")
        }
    }

    override fun getRoutesLocation(origin: Coordinate, destination: Coordinate): Result<Routes> {
        return fetcher.getRoutes(origin, destination).map {
            Mapper.mapRoutesHereToRoutes(it)
        }
    }

    override fun calculateDistance(origin: Coordinate, destination: Coordinate): Result<Double> {
        return getRoutesLocation(origin, destination).map {
            it.distanceInMeter
        }
    }

    override fun findDriverFromCoordinate(coordinate: Coordinate): Result<List<User>> {
        return userRepository.findDriversByCoordinate(coordinate)
    }
}