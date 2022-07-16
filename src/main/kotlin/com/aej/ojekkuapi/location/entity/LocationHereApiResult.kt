package com.aej.ojekkuapi.location.entity


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

// HERE API Response
@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationHereApiResult(
    @JsonProperty("items")
    var items: List<Item?>?
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Item(
        @JsonProperty("access")
        var access: List<Acces?>?,
        @JsonProperty("address")
        var address: Address?,
        @JsonProperty("categories")
        var categories: List<Category?>?,
        @JsonProperty("contacts")
        var contacts: List<Contact?>?,
        @JsonProperty("distance")
        var distance: Int?,
        @JsonProperty("id")
        var id: String?,
        @JsonProperty("language")
        var language: String?,
        @JsonProperty("ontologyId")
        var ontologyId: String?,
        @JsonProperty("position")
        var position: Position?,
        @JsonProperty("references")
        var references: List<Reference?>?,
        @JsonProperty("resultType")
        var resultType: String?,
        @JsonProperty("title")
        var title: String?
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Acces(
            @JsonProperty("lat")
            var lat: Double?,
            @JsonProperty("lng")
            var lng: Double?
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Address(
            @JsonProperty("city")
            var city: String?,
            @JsonProperty("countryCode")
            var countryCode: String?,
            @JsonProperty("countryName")
            var countryName: String?,
            @JsonProperty("county")
            var county: String?,
            @JsonProperty("district")
            var district: String?,
            @JsonProperty("houseNumber")
            var houseNumber: String?,
            @JsonProperty("label")
            var label: String?,
            @JsonProperty("postalCode")
            var postalCode: String?,
            @JsonProperty("state")
            var state: String?,
            @JsonProperty("street")
            var street: String?,
            @JsonProperty("subdistrict")
            var subdistrict: String?
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Category(
            @JsonProperty("id")
            var id: String?,
            @JsonProperty("name")
            var name: String?,
            @JsonProperty("primary")
            var primary: Boolean?
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Contact(
            @JsonProperty("phone")
            var phone: List<Phone?>?,
            @JsonProperty("www")
            var www: List<Www?>?
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Phone(
                @JsonProperty("value")
                var value: String?
            )

            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Www(
                @JsonProperty("categories")
                var categories: List<Category?>?,
                @JsonProperty("value")
                var value: String?
            ) {
                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Category(
                    @JsonProperty("id")
                    var id: String?
                )
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Position(
            @JsonProperty("lat")
            var lat: Double?,
            @JsonProperty("lng")
            var lng: Double?
        )

        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Reference(
            @JsonProperty("id")
            var id: String?,
            @JsonProperty("supplier")
            var supplier: Supplier?
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Supplier(
                @JsonProperty("id")
                var id: String?
            )
        }
    }
}