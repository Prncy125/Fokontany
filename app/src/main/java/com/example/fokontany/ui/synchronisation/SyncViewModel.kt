package com.example.fokontany.ui.synchronisation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fokontany.data.local.repository.DistributionAideRepository
import com.example.fokontany.data.local.repository.FoyerRepository
import com.example.fokontany.data.local.repository.ProgrammeAideRepository
import com.example.fokontany.data.sync.SyncManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class EtatSynchronisation {
    IDLE,
    EN_COURS,
    SUCCES,
    ERREUR
}

class SyncViewModel(
    private val syncManager: SyncManager,
    foyerRepository: FoyerRepository,
    programmeAideRepository: ProgrammeAideRepository,
    distributionAideRepository: DistributionAideRepository
) : ViewModel() {

    val pending: StateFlow<Int> =
        combine(
            foyerRepository.compterPending(),
            foyerRepository.compterHabitantsPending(),
            programmeAideRepository.compterPending(),
            distributionAideRepository.compterPending()
        ) { foyers, habitants, programmes, distributions ->
            foyers + habitants + programmes + distributions
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0
        )

    val errors: StateFlow<Int> =
        combine(
            foyerRepository.compterErrors(),
            foyerRepository.compterHabitantsErrors(),
            programmeAideRepository.compterErrors(),
            distributionAideRepository.compterErrors()
        ) { foyers, habitants, programmes, distributions ->
            foyers + habitants + programmes + distributions
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0
        )

    private val _etat =
        kotlinx.coroutines.flow.MutableStateFlow(EtatSynchronisation.IDLE)
    val etat: StateFlow<EtatSynchronisation> = _etat

    private val _message =
        kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun synchroniser() {
        if (_etat.value == EtatSynchronisation.EN_COURS) return

        viewModelScope.launch {
            _etat.value = EtatSynchronisation.EN_COURS
            _message.value = null

            val resultat = syncManager.synchroniser()

            if (resultat.isSuccess) {
                _etat.value = EtatSynchronisation.SUCCES
                _message.value = "Synchronisation réussie"
            } else {
                _etat.value = EtatSynchronisation.ERREUR
                _message.value =
                    resultat.exceptionOrNull()?.message
                        ?: "Échec de la synchronisation"
            }
        }
    }
}