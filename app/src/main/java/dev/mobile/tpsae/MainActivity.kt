package dev.mobile.tpsae

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.mobile.tpsae.data.TmdbRepository
import dev.mobile.tpsae.ui.theme.TpSaeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TpSaeTheme {

                //test temporaire
                LaunchedEffect(Unit) {
                    try {
                        Log.d("TMDB_TEST", "Lancement de la requête...")
                        val movies = TmdbRepository.searchMovies("Avatar")

                        Log.d("TMDB_TEST", "Succès ! ${movies.size} films trouvés.")
                        movies.forEach { movie ->
                            Log.d("TMDB_TEST", "Film : ${movie.title} (Date : ${movie.releaseDate})")
                        }
                    } catch (e: Exception) {
                        Log.e("TMDB_TEST", "Erreur de connexion : ${e.message}")
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Test en cours... Voir logcat")
                }
            }
        }
    }
}