package dev.mobile.tpsae.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieResponse(
    val results: List<Movie>
)