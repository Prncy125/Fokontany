package com.example.fokontany.ui.recherche

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fokontany.data.local.relation.HabitantAvecFoyer
import com.example.fokontany.data.local.repository.FoyerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class RechercheViewModel(
    private val repository: FoyerRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")

    val query: StateFlow<String> = _query

    val resultats: StateFlow<List<HabitantAvecFoyer>> =
        _query
            .debounce(300L)
            .flatMapLatest { terme ->
                if (terme.isBlank()) {
                    flowOf(emptyList())
                } else {
                    repository.rechercherHabitants(terme.trim())
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun onQueryChange(nouvelleValeur: String) {
        _query.value = nouvelleValeur
    }
}
