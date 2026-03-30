package dev.mobile.tpsae

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mobile.tpsae.ui.theme.TpSaeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TpSaeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // On affiche notre nouvelle vue d'accueil
                    HomeScreen { query ->
                        // Quand on clique sur le bouton, on ouvre ListActivity avec la recherche en entrée
                        val intent = Intent(this, ListActivity::class.java).apply {
                            putExtra("SEARCH_QUERY", query)
                        }
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun HomeScreen(onSearch: (String) -> Unit) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenue",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Recherchez un film")

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Titre du film...") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (query.isNotBlank()) {
                    onSearch(query)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Chercher")
        }
    }
}