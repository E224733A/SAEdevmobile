package dev.mobile.tpsae

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.ui.SearchState
import dev.mobile.tpsae.ui.SearchViewModel
import dev.mobile.tpsae.ui.theme.TpSaeTheme

class ListActivity : ComponentActivity() {

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // On récupère le texte tapé dans MainActivity
        val query = intent.getStringExtra("SEARCH_QUERY") ?: ""

        setContent {
            TpSaeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ListScreen(viewModel, query) { movie ->
                        // Clic sur un film qui dirige vers DetailActivity
                        val intent = Intent(this, DetailActivity::class.java).apply {
                            putExtra("MOVIE_ID", movie.id)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun ListScreen(viewModel: SearchViewModel, query: String, onMovieClick: (Movie) -> Unit) {
    val state by viewModel.state.collectAsState()

    // Dès que la page s'ouvre, on lance la recherche automatiquement
    LaunchedEffect(query) {
        if (query.isNotBlank()) {
            viewModel.search(query)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Résultats pour : \"$query\"", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        when (val currentState = state) {
            is SearchState.Idle -> Text("Recherche en cours...")
            is SearchState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is SearchState.Empty -> Text("Aucun film trouvé.")
            is SearchState.Error -> Text("Erreur : ${currentState.message}", color = MaterialTheme.colorScheme.error)
            is SearchState.Success -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(currentState.movies) { movie ->
                        MovieItem(movie = movie, onClick = { onMovieClick(movie) })
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            if (movie.posterPath != null) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w200${movie.posterPath}",
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) { Text("Pas d'image") }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Sortie : ${movie.releaseDate ?: "Inconnue"}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Note : ${movie.voteAverage ?: "N/A"}/10", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}