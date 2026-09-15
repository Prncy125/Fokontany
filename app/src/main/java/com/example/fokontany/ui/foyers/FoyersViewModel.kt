package com.example.fokontany.ui.foyers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.repository.FoyerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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