package com.example.fokontany.ui.distribution

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fokontany.data.local.entity.DistributionAideEntity
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.data.local.entity.ProgrammeAideEntity
import com.example.fokontany.data.local.repository.DistributionAideRepository
import com.example.fokontany.data.local.repository.FoyerRepository
import com.example.fokontany.data.local.repository.ProgrammeAideRepository
import com.example.fokontany.domain.model.StatutDistribution
import com.example.fokontany.domain.model.SyncStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class EtapeDistribution { PROGRAMME, FOYER, DETAILS }
enum class TypeAide { MONTANT, QUANTITE }
sealed class EnregistrementResultat {
    data object Succes : EnregistrementResultat()
    data class DoubleDistribution(val message: String) : EnregistrementResultat()
}

class DistributionViewModel(
    private val distributionRepository: DistributionAideRepository,
    private val programmeRepository: ProgrammeAideRepository,
    private val foyerRepository: FoyerRepository
) : ViewModel() {

    val distributions: StateFlow<List<DistributionAideEntity>> =
        distributionRepository.observerTous()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val programmes: StateFlow<List<ProgrammeAideEntity>> =
        programmeRepository.observerTous()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val foyers: StateFlow<List<FoyerEntity>> =
        foyerRepository.observerTous()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    private val _etape = MutableStateFlow(EtapeDistribution.PROGRAMME)
    val etape: StateFlow<EtapeDistribution> = _etape

    private val _programmeSelectionne = MutableStateFlow<ProgrammeAideEntity?>(null)
    val programmeSelectionne: StateFlow<ProgrammeAideEntity?> = _programmeSelectionne

    private val _foyerSelectionne = MutableStateFlow<FoyerEntity?>(null)
    val foyerSelectionne: StateFlow<FoyerEntity?> = _foyerSelectionne

    private val _representant = MutableStateFlow<HabitantEntity?>(null)
    val representant: StateFlow<HabitantEntity?> = _representant

    private val _dateDistribution = MutableStateFlow("")
    val dateDistribution: StateFlow<String> = _dateDistribution

    private val _montant = MutableStateFlow("")
    val montant: StateFlow<String> = _montant

    private val _quantite = MutableStateFlow("")
    val quantite: StateFlow<String> = _quantite

    private val _typeAide = MutableStateFlow(TypeAide.MONTANT)
    val typeAide: StateFlow<TypeAide> = _typeAide

    private val _resultat = MutableStateFlow<EnregistrementResultat?>(null)
    val resultat: StateFlow<EnregistrementResultat?> = _resultat

    fun onProgrammeSelectionne(programme: ProgrammeAideEntity) {
        _programmeSelectionne.value = programme
        _etape.value = EtapeDistribution.FOYER
    }

    fun onFoyerSelectionne(foyer: FoyerEntity) {
        _foyerSelectionne.value = foyer
        _etape.value = EtapeDistribution.DETAILS

        viewModelScope.launch {
            val rep = foyerRepository.trouverRepresentant(foyer.idLocal)
            _representant.value = rep
        }
    }

    fun onDateChange(date: String) {
        _dateDistribution.value = date
    }

    fun onMontantChange(montant: String) {
        _montant.value = montant
    }

    fun onQuantiteChange(quantite: String) {
        _quantite.value = quantite
    }

    fun onTypeAideChange(type: TypeAide) {
        _typeAide.value = type
    }

    fun onRetour() {
        when (_etape.value) {
            EtapeDistribution.FOYER -> {
                _etape.value = EtapeDistribution.PROGRAMME
                _foyerSelectionne.value = null
                _representant.value = null
            }
            EtapeDistribution.DETAILS -> {
                _etape.value = EtapeDistribution.FOYER
                _dateDistribution.value = ""
                _montant.value = ""
                _quantite.value = ""
                _representant.value = null
            }
            EtapeDistribution.PROGRAMME -> {}
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun enregistrer() {
        val programme = _programmeSelectionne.value ?: return
        val foyer = _foyerSelectionne.value ?: return
        val rep = _representant.value ?: return
        val date = _dateDistribution.value.trim()

        if (date.isBlank()) return

        val montantValue = _montant.value.trim().toLongOrNull()
        val quantiteValue = _quantite.value.trim().toIntOrNull()

        viewModelScope.launch {
            val distribution = DistributionAideEntity(
                programmeAideId = programme.id,
                foyerId = foyer.idLocal,
                representantHabitantId = rep.id,
                dateDistribution = date,
                montant = if (_typeAide.value == TypeAide.MONTANT) montantValue else null,
                quantite = if (_typeAide.value == TypeAide.QUANTITE) quantiteValue else null,
                statut = StatutDistribution.RECUPEREE,
                syncStatus = SyncStatus.PENDING
            )

            val resultat = distributionRepository.enregistrerSiAbsente(distribution)

            _resultat.value = if (resultat != null) {
                EnregistrementResultat.Succes
            } else {
                EnregistrementResultat.DoubleDistribution(
                    "Ce foyer a deja recu cette aide."
                )
            }
        }
    }

    fun reinitialiser() {
        _etape.value = EtapeDistribution.PROGRAMME
        _programmeSelectionne.value = null
        _foyerSelectionne.value = null
        _representant.value = null
        _dateDistribution.value = ""
        _montant.value = ""
        _quantite.value = ""
        _typeAide.value = TypeAide.MONTANT
        _resultat.value = null
    }

    fun onResultatConsomme() {
        _resultat.value = null
    }
}