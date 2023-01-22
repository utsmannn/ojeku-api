package com.aej.ojekkuapi.location.entity

data class Location(
    var name: String = "",
    var address: Address = Address(),
    var coordinate: Coordinate = Coordinate()
) {
    data class Address(
        var city: String = "",
        var country: String = "",
        var district: String = ""
    )
}