package dev.mobile.tpsae.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.mobile.tpsae.data.TmdbRepository
import dev.mobile.tpsae.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

//Etats possibles pour la page de détail
sealed class DetailState {
    object Loading : DetailState()
    data class Success(val movie: Movie) : DetailState()
    data class Error(val message: String) : DetailState()
}

class DetailViewModel : ViewModel() {
    private val _state = MutableStateFlow<DetailState>(DetailState.Loading)
    val state: StateFlow<DetailState> = _state.asStateFlow()

    fun loadMovie(movieId: Int) {
        _state.value = DetailState.Loading
        viewModelScope.launch {
            try {
                // On prend les détails via l'ID
                val movie = TmdbRepository.getMovieDetails(movieId)
                _state.value = DetailState.Success(movie)
            } catch (e: Exception) {
                _state.value = DetailState.Error("Impossible de charger les détails de ce film.")
            }
        }
    }
}