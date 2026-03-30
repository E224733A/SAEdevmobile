package dev.mobile.tpsae.data

import dev.mobile.tpsae.BuildConfig
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.model.MovieResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object TmdbRepository {
    private const val BASE_URL = "https://api.themoviedb.org/3"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    private fun requireApiKey() {
        check(BuildConfig.TMDB_API_KEY.isNotBlank()) {
            "TMDB_API_KEY manquante. Ajoute-la dans local.properties."
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        requireApiKey()

        return client.get("$BASE_URL/search/movie") {
            parameter("api_key", BuildConfig.TMDB_API_KEY)
            parameter("query", query)
            parameter("language", "fr-FR")
        }.body<MovieResponse>().results
    }

    suspend fun getMovieDetails(id: Int): Movie {
        requireApiKey()

        return client.get("$BASE_URL/movie/$id") {
            parameter("api_key", BuildConfig.TMDB_API_KEY)
            parameter("language", "fr-FR")
        }.body()
    }
}