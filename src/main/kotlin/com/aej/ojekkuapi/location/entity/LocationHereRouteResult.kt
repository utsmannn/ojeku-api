package com.aej.ojekkuapi.location.entity


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class LocationHereRouteResult(
    @JsonProperty("routes")
    var routes: List<Route?>?
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Route(
        @JsonProperty("id")
        var id: String?,
        @JsonProperty("sections")
        var sections: List<Section?>?
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Section(
            @JsonProperty("arrival")
            var arrival: Arrival?,
            @JsonProperty("departure")
            var departure: Departure?,
            @JsonProperty("id")
            var id: String?,
            @JsonProperty("summary")
            var summary: Summary?,
            @JsonProperty("polyline")
            var polyline: String?,
            @JsonProperty("transport")
            var transport: Transport?,
            @JsonProperty("type")
            var type: String?
        ) {
            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Arrival(
                @JsonProperty("place")
                var place: Place?,
                @JsonProperty("time")
                var time: String?
            ) {
                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Place(
                    @JsonProperty("location")
                    var location: Location?,
                    @JsonProperty("originalLocation")
                    var originalLocation: OriginalLocation?,
                    @JsonProperty("type")
                    var type: String?
                ) {
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    data class Location(
                        @JsonProperty("lat")
                        var lat: Double?,
                        @JsonProperty("lng")
                        var lng: Double?
                    )

                    @JsonIgnoreProperties(ignoreUnknown = true)
                    data class OriginalLocation(
                        @JsonProperty("lat")
                        var lat: Double?,
                        @JsonProperty("lng")
                        var lng: Double?
                    )
                }
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Departure(
                @JsonProperty("place")
                var place: Place?,
                @JsonProperty("time")
                var time: String?
            ) {
                @JsonIgnoreProperties(ignoreUnknown = true)
                data class Place(
                    @JsonProperty("location")
                    var location: Location?,
                    @JsonProperty("originalLocation")
                    var originalLocation: OriginalLocation?,
                    @JsonProperty("type")
                    var type: String?
                ) {
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    data class Location(
                        @JsonProperty("lat")
                        var lat: Double?,
                        @JsonProperty("lng")
                        var lng: Double?
                    )

                    @JsonIgnoreProperties(ignoreUnknown = true)
                    data class OriginalLocation(
                        @JsonProperty("lat")
                        var lat: Double?,
                        @JsonProperty("lng")
                        var lng: Double?
                    )
                }
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Transport(
                @JsonProperty("mode")
                var mode: String?
            )

            @JsonIgnoreProperties(ignoreUnknown = true)
            data class Summary(
                @JsonProperty("duration")
                var duration: Long,
                @JsonProperty("length")
                var length: Long,
                @JsonProperty("baseDuration")
                var baseDuration: Long
            )
        }
    }
}