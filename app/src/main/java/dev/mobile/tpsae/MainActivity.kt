package dev.mobile.tpsae

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
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.mobile.tpsae.model.Movie
import dev.mobile.tpsae.ui.SearchState
import dev.mobile.tpsae.ui.SearchViewModel
import dev.mobile.tpsae.ui.theme.TpSaeTheme

class MainActivity : ComponentActivity() {

    //Connexion du ViewModel à l'Activité
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TpSaeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Lancement de l'écran principal
                    SearchScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: SearchViewModel) {
    //On écoute l'état du ViewModel
    val state by viewModel.state.collectAsState()

    val context = LocalContext.current

    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        //Barre de recherche
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                viewModel.search(it) // À chaque lettre tapée, on relance la recherche
            },
            label = { Text("Rechercher un film...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Affichage dynamique selon l'état de la recherche
        when (val currentState = state) {
            is SearchState.Idle -> {
                Text("Saisissez un titre pour commencer la recherche.")
            }
            is SearchState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is SearchState.Empty -> {
                Text("Aucun film trouvé pour \"$query\".")
            }
            is SearchState.Error -> {
                Text("Erreur : ${currentState.message}", color = MaterialTheme.colorScheme.error)
            }
            is SearchState.Success -> {
                //Liste des films
                LazyColumn {
                    items(currentState.movies) { movie ->
                        MovieItem(movie = movie) {
                            val intent = Intent(context, DetailActivity::class.java).apply {
                                //On renseigne l'ID du film pour afficher le bon film
                                putExtra("MOVIE_ID", movie.id)
                            }
                            context.startActivity(intent)
                        }
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
            //Affiche du film chargée depuis internet
            if (movie.posterPath != null) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w200${movie.posterPath}",
                    contentDescription = "Affiche de ${movie.title}",
                    modifier = Modifier.size(100.dp)
                )
            } else {
                Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                    Text("Pas d'image")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            //Informations du film
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