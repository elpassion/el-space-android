package pl.elpassion.space.pacman.model

import com.fasterxml.jackson.annotation.JsonProperty

data class LocationUpdateEvent(
        @JsonProperty
        val playerId: String,
        @JsonProperty
        val latitude: Double,
        @JsonProperty
        val longitude: Double
)