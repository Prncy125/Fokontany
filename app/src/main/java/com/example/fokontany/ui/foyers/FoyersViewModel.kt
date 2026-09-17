package com.example.fokontany.ui.foyers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fokontany.data.local.entity.FoyerEntity
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
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class FoyersViewModel(
    private val repository: FoyerRepository
) : ViewModel() {

    val foyers: StateFlow<List<FoyerEntity>> =
        repository.observerTous()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _rechercheQuery = MutableStateFlow("")

    val rechercheQuery: StateFlow<String> = _rechercheQuery

    val resultatsRecherche: StateFlow<List<HabitantAvecFoyer>> =
        _rechercheQuery
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

    fun onRechercheChange(nouvelleValeur: String) {
        _rechercheQuery.value = nouvelleValeur
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ajouterFoyer(
        adresse: String,
        quartier: String
    ) {
        viewModelScope.launch {
            val foyer = FoyerEntity(
                adresse = adresse,
                quartier = quartier,
                dateEnregistrement = java.text.SimpleDateFormat(
                    "yyyy-MM-dd",
                    java.util.Locale.getDefault()
                ).format(java.util.Date())
            )

            repository.inserer(foyer)
        }
    }

    fun modifierFoyer(
        foyer: FoyerEntity,
        adresse: String,
        quartier: String
    ) {
        viewModelScope.launch {
            val foyerModifie = foyer.copy(
                adresse = adresse.trim(),
                quartier = quartier.trim(),
                syncStatus = com.example.fokontany.domain.model.SyncStatus.PENDING
            )

            repository.modifierFoyer(foyerModifie)
        }
    }

    fun desactiverFoyer(foyerId: Long) {
        viewModelScope.launch {
            repository.desactiverFoyer(foyerId)
        }
    }
}
