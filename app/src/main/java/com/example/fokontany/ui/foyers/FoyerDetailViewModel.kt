package com.example.fokontany.ui.foyers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.data.local.relation.FoyerAvecHabitants
import com.example.fokontany.data.local.repository.FoyerRepository
import com.example.fokontany.domain.model.SyncStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FoyerDetailViewModel(
    private val repository: FoyerRepository,
    private val foyerId: Long
) : ViewModel() {

    val foyer: StateFlow<FoyerAvecHabitants?> =
        repository.observerAvecHabitants(foyerId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null
            )

    fun ajouterHabitant(
        nom: String,
        prenom: String,
        sexe: String,
        dateNaissance: String,
        telephone: String?,
        codePaysTelephone: String
    ) {
        viewModelScope.launch {
            val habitant = HabitantEntity(
                foyerId = foyerId,
                nom = nom,
                prenom = prenom,
                sexe = sexe,
                dateNaissance = dateNaissance,
                telephone = telephone,
                codePaysTelephone = codePaysTelephone
            )
            repository.insererHabitant(habitant)
        }
    }

    fun definirRepresentant(habitantId: Long) {
        viewModelScope.launch {
            try {
                repository.definirRepresentant(
                    foyerId = foyerId,
                    habitantId = habitantId
                )
            } catch (e: IllegalArgumentException) {
                // La règle métier empêche l'opération.
                // On évite que l'exception fasse planter l'application.
            }
        }
    }
    fun modifierFoyer(
        adresse: String,
        quartier: String
    ) {
        viewModelScope.launch {
            val foyerActuel = foyer.value?.foyer ?: return@launch

            val foyerModifie = foyerActuel.copy(
                adresse = adresse.trim(),
                quartier = quartier.trim(),
                syncStatus = SyncStatus.PENDING
            )

            repository.modifierFoyer(foyerModifie)
        }
    }

    fun desactiverFoyer() {
        viewModelScope.launch {
            repository.desactiverFoyer(foyerId)
        }
    }

    fun modifierHabitant(
        habitant: HabitantEntity,
        nom: String,
        prenom: String,
        sexe: String,
        dateNaissance: String,
        telephone: String?,
        codePaysTelephone: String
    ) {
        viewModelScope.launch {
            val habitantModifie = habitant.copy(
                nom = nom.trim(),
                prenom = prenom.trim(),
                sexe = sexe,
                dateNaissance = dateNaissance,
                telephone = telephone?.trim()?.ifBlank { null },
                codePaysTelephone = codePaysTelephone,
                syncStatus = SyncStatus.PENDING
            )

            repository.modifierHabitant(habitantModifie)
        }
    }

    fun desactiverHabitant(habitantId: Long) {
        viewModelScope.launch {
            repository.desactiverHabitant(habitantId)
        }
    }

    fun activerHabitant(habitantId: Long) {
        viewModelScope.launch {
            repository.activerHabitant(habitantId)
        }
    }
}