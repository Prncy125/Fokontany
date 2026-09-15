package com.example.fokontany.ui.aides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fokontany.data.local.entity.ProgrammeAideEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifierProgrammeAideScreen(
    viewModel: ProgrammesAideViewModel,
    programmeId: Long,
    onProgrammeModifie: () -> Unit,
    onRetour: () -> Unit
) {
    var programme by remember { mutableStateOf<ProgrammeAideEntity?>(null) }

    var nom by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateDebut by remember { mutableStateOf("") }

    var afficherCalendrier by remember { mutableStateOf(false) }
    var erreurNom by remember { mutableStateOf(false) }
    var erreurDate by remember { mutableStateOf(false) }

    LaunchedEffect(programmeId) {
        val resultat = viewModel.trouverParId(programmeId)

        programme = resultat

        if (resultat != null) {
            nom = resultat.nom
            description = resultat.description.orEmpty()
            dateDebut = convertirDatePourAffichage(resultat.dateDebut)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Modifier le programme",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        if (programme == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Programme introuvable.",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Icon(
                    Icons.Default.VolunteerActivism,
                    contentDescription = null
                )

                Text(
                    text = "Informations du programme",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = nom,
                    onValueChange = {
                        nom = it
                        erreurNom = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Nom du programme")
                    },
                    singleLine = true,
                    isError = erreurNom,
                    supportingText = {
                        if (erreurNom) {
                            Text("Le nom du programme est obligatoire.")
                        }
                    }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Description")
                    },
                    minLines = 3,
                    maxLines = 5
                )

                OutlinedTextField(
                    value = dateDebut,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Date de début")
                    },
                    placeholder = {
                        Text("JJ/MM/AAAA")
                    },
                    readOnly = true,
                    isError = erreurDate,
                    supportingText = {
                        if (erreurDate) {
                            Text("La date de début est obligatoire.")
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                afficherCalendrier = true
                            }
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = "Choisir la date"
                            )
                        }
                    }
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (nom.trim().isBlank()) {
                            erreurNom = true
                            return@Button
                        }

                        if (dateDebut.isBlank()) {
                            erreurDate = true
                            return@Button
                        }

                        val programmeActuel = programme
                            ?: return@Button

                        viewModel.modifierProgramme(
                            programme = programmeActuel,
                            nom = nom,
                            description = description,
                            dateDebut = convertirDatePourBase(dateDebut)
                        )

                        onProgrammeModifie()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enregistrer les modifications")
                }
            }
        }
    }

    if (afficherCalendrier) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                afficherCalendrier = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis

                        if (millis != null) {
                            val date = Date(millis)

                            val format = SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            )

                            dateDebut = format.format(date)
                            erreurDate = false
                        }

                        afficherCalendrier = false
                    }
                ) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        afficherCalendrier = false
                    }
                ) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }
}

private fun convertirDatePourAffichage(date: String): String {
    return try {
        val formatBase = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        val formatAffichage = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        val parsedDate = formatBase.parse(date)

        if (parsedDate != null) {
            formatAffichage.format(parsedDate)
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}

private fun convertirDatePourBase(date: String): String {
    return try {
        val formatAffichage = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )

        val formatBase = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        )

        val parsedDate = formatAffichage.parse(date)

        if (parsedDate != null) {
            formatBase.format(parsedDate)
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}