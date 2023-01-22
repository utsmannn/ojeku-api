package com.aej.ojekkuapi.user.repository

import com.aej.ojekkuapi.DatabaseComponent
import com.aej.ojekkuapi.location.entity.Coordinate
import com.aej.ojekkuapi.toResult
import com.aej.ojekkuapi.user.entity.UserLocation
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.geojson.Point
import com.mongodb.client.model.geojson.Position
import org.litote.kmongo.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserLocationRepositoryImpl(
    @Autowired
    private val databaseComponent: DatabaseComponent
) : UserLocationRepository {

    private fun getCollection(): MongoCollection<UserLocation> {
        return databaseComponent.database.getDatabase("ojeku")
            .getCollection()
    }

    override fun updateLocation(userId: String, coordinate: Coordinate): Result<UserLocation> {
        val isExistUserLocation = getUserLocation(userId).isSuccess
        if (isExistUserLocation) {
            getCollection().updateOne(
                UserLocation::id eq userId,
                UserLocation::coordinate setTo coordinate
            )
        } else {
            getCollection().insertOne(UserLocation(userId, coordinate))
        }

        return getUserLocation(userId)
    }

    override fun getUserLocation(userId: String): Result<UserLocation> {
        return getCollection().findOne(UserLocation::id eq userId).toResult()
    }

    override fun findDriverByCoordinate(coordinate: Coordinate): Result<List<UserLocation>> {
        val point = Point(Position(coordinate.longitude, coordinate.latitude))
        getCollection().createIndex(geo2dsphere(UserLocation::lngLat))
        return getCollection().find(
            UserLocation::lngLat.nearSphere(point, 2000.0)
        ).toList().toResult()
    }

    // val point = Point(Position(coordinate.longitude, coordinate.latitude))
    //        getCollection().createIndex(geo2dsphere(User::lastLocation / Location::coordinates))
    //        return getCollection().find((User::lastLocation / Location::coordinates).nearSphere(point, 2000.0)).toList().toResult()


}