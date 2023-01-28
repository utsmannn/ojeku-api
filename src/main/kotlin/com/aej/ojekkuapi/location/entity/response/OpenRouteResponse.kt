package com.aej.ojekkuapi.location.entity.response


import com.fasterxml.jackson.annotation.JsonProperty

data class OpenRouteResponse(
    @JsonProperty("bbox")
    val bbox: List<Double?>?,
    @JsonProperty("features")
    val features: List<Feature?>?,
    @JsonProperty("metadata")
    val metadata: Metadata?,
    @JsonProperty("type")
    val type: String?
) {
    data class Feature(
        @JsonProperty("bbox")
        val bbox: List<Double?>?,
        @JsonProperty("geometry")
        val geometry: Geometry?,
        @JsonProperty("properties")
        val properties: Properties?,
        @JsonProperty("type")
        val type: String?
    ) {
        data class Geometry(
            @JsonProperty("coordinates")
            val coordinates: List<List<Double?>?>?,
            @JsonProperty("type")
            val type: String?
        )

        data class Properties(
            @JsonProperty("segments")
            val segments: List<Segment?>?,
            @JsonProperty("summary")
            val summary: Summary?,
            @JsonProperty("way_points")
            val wayPoints: List<Int?>?
        ) {
            data class Segment(
                @JsonProperty("distance")
                val distance: Double?,
                @JsonProperty("duration")
                val duration: Double?,
                @JsonProperty("steps")
                val steps: List<Step?>?
            ) {
                data class Step(
                    @JsonProperty("distance")
                    val distance: Double?,
                    @JsonProperty("duration")
                    val duration: Double?,
                    @JsonProperty("instruction")
                    val instruction: String?,
                    @JsonProperty("name")
                    val name: String?,
                    @JsonProperty("type")
                    val type: Int?,
                    @JsonProperty("way_points")
                    val wayPoints: List<Int?>?
                )
            }

            data class Summary(
                @JsonProperty("distance")
                val distance: Double?,
                @JsonProperty("duration")
                val duration: Double?
            )
        }
    }

    data class Metadata(
        @JsonProperty("attribution")
        val attribution: String?,
        @JsonProperty("engine")
        val engine: Engine?,
        @JsonProperty("query")
        val query: Query?,
        @JsonProperty("service")
        val service: String?,
        @JsonProperty("timestamp")
        val timestamp: Long?
    ) {
        data class Engine(
            @JsonProperty("build_date")
            val buildDate: String?,
            @JsonProperty("graph_date")
            val graphDate: String?,
            @JsonProperty("version")
            val version: String?
        )

        data class Query(
            @JsonProperty("coordinates")
            val coordinates: List<List<Double?>?>?,
            @JsonProperty("format")
            val format: String?,
            @JsonProperty("profile")
            val profile: String?
        )
    }
}