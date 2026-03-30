package dev.mobile.tpsae

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.mobile.tpsae.ui.DetailState
import dev.mobile.tpsae.ui.DetailViewModel
import dev.mobile.tpsae.ui.theme.TpSaeTheme

class DetailActivity : ComponentActivity() {

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // On récupère l'ID du film envoyé par la MainActivity
        val movieId = intent.getIntExtra("MOVIE_ID", -1)

        setContent {
            TpSaeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DetailScreen(viewModel, movieId)
                }
            }
        }
    }
}

@Composable
fun DetailScreen(viewModel: DetailViewModel, movieId: Int) {
    // Dès que l'écran s'ouvre, on demande au ViewModel de charger le film
    LaunchedEffect(movieId) {
        if (movieId != -1) {
            viewModel.loadMovie(movieId)
        }
    }

    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val currentState = state) {
            is DetailState.Loading -> CircularProgressIndicator()
            is DetailState.Error -> Text(currentState.message, color = MaterialTheme.colorScheme.error)
            is DetailState.Success -> {
                val movie = currentState.movie
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    if (movie.posterPath != null) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                            contentDescription = "Affiche en grand",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = movie.title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Date de sortie : ${movie.releaseDate ?: "Inconnue"}", color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Note : ${movie.voteAverage ?: "N/A"} / 10", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Synopsis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = movie.overview)
                }
            }
        }
    }
}