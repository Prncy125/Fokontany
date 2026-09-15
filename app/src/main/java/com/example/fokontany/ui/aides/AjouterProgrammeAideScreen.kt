package com.example.fokontany.ui.aides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjouterProgrammeAideScreen(
    viewModel: ProgrammesAideViewModel,
    onProgrammeAjoute: () -> Unit,
    onRetour: () -> Unit
) {
    var nom by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var dateDebut by remember {
        mutableStateOf("")
    }

    var afficherCalendrier by remember {
        mutableStateOf(false)
    }

    var erreurNom by remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nouveau programme d'aide",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onRetour
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Icon(
                imageVector = Icons.Default.VolunteerActivism,
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
                placeholder = {
                    Text("Ex. Aide alimentaire")
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
                placeholder = {
                    Text("Description du programme")
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
                trailingIcon = {
                    IconButton(
                        onClick = {
                            afficherCalendrier = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
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
                        return@Button
                    }

                    viewModel.ajouterProgramme(
                        nom = nom,
                        description = description,
                        dateDebut = convertirDatePourBase(dateDebut)
                    )

                    onProgrammeAjoute()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer le programme")
            }
        }
    }

    if (afficherCalendrier) {

        val datePickerState = androidx.compose.material3.rememberDatePickerState()

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

private fun convertirDatePourBase(
    date: String
): String {
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