package dev.mobile.tpsae.data

import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.model.MovieResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object TmdbRepository {
    private const val BASE_URL = "https://api.themoviedb.org/3"

    private const val API_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJiOTEzZjgzYzNjYWJiMDFlYzhhNjkwNjA3NTNiOWE1MyIsIm5iZiI6MTc3NDM0Mzk5OC43MzYsInN1YiI6IjY5YzI1NzNlZWYyMjkyYjhlYmI2N2Y0OSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.KFqetJXDRya1TRcvtDo9QYcLy7JMHbozm8NeQXRIfHk"

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return client.get("$BASE_URL/search/movie") {
            url {
                parameters.append("query", query)
                parameters.append("language", "fr-FR")
            }
            header("Authorization", "Bearer $API_TOKEN")
        }.body<MovieResponse>().results
    }

    suspend fun getMovieDetails(id: Int): Movie {
        return client.get("$BASE_URL/movie/$id") {
            url { parameters.append("language", "fr-FR") }
            header("Authorization", "Bearer $API_TOKEN")
        }.body()
    }
}