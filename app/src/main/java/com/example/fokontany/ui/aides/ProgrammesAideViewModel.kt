package com.example.fokontany.ui.aides

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fokontany.data.local.entity.ProgrammeAideEntity
import com.example.fokontany.data.local.repository.ProgrammeAideRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgrammesAideViewModel(
    private val repository: ProgrammeAideRepository
) : ViewModel() {

    val programmes: StateFlow<List<ProgrammeAideEntity>> =
        repository.observerTous().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun ajouterProgramme(
        nom: String,
        description: String?,
        dateDebut: String
    ) {
        viewModelScope.launch {
            val programme = ProgrammeAideEntity(
                nom = nom.trim(),
                description = description
                    ?.trim()
                    ?.ifBlank { null },
                dateDebut = dateDebut
            )

            repository.inserer(programme)
        }
    }

    fun modifierProgramme(
        programme: ProgrammeAideEntity,
        nom: String,
        description: String?,
        dateDebut: String
    ) {
        viewModelScope.launch {
            val programmeModifie = programme.copy(
                nom = nom.trim(),
                description = description
                    ?.trim()
                    ?.ifBlank { null },
                dateDebut = dateDebut
            )

            repository.modifier(programmeModifie)
        }
    }
    suspend fun trouverParId(id: Long): ProgrammeAideEntity? {
        return repository.trouverParId(id)
    }
}