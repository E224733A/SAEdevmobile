package dev.mobile.tpsae.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mobile.tpsae.data.TmdbRepository
import dev.mobile.tpsae.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//Définition des états possibles de notre écran de recherche
sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    object Empty : SearchState()
    data class Success(val movies: List<Movie>) : SearchState()
    data class Error(val message: String) : SearchState()
}

//Logique du ViewModel
class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun search(query: String) {
        if (query.isBlank()) {
            _state.value = SearchState.Idle
            return
        }

        _state.value = SearchState.Loading

        viewModelScope.launch {
            try {
                val results = TmdbRepository.searchMovies(query)
                if (results.isEmpty()) {
                    _state.value = SearchState.Empty
                } else {
                    _state.value = SearchState.Success(results)
                }
            } catch (e: Exception) {
                _state.value = SearchState.Error("Erreur lors de la recherche : ${e.message}")
            }
        }
    }
}