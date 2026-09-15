package com.example.fokontany.ui.foyers

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rejowan.ccpc.CountryCodePickerTextField
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjouterHabitantScreen(
    viewModel: FoyerDetailViewModel,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onHabitantAjoute: () -> Unit
) {
    var nom by remember {
        mutableStateOf("")
    }

    var prenom by remember {
        mutableStateOf("")
    }

    var sexe by remember {
        mutableStateOf("")
    }

    var dateNaissance by remember {
        mutableStateOf("")
    }

    var telephone by remember {
        mutableStateOf("")
    }

    var telephoneValide by remember {
        mutableStateOf(true)
    }

    var sexeMenuOuvert by remember {
        mutableStateOf(false)
    }

    var codePaysTelephone by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current

    val calendar = remember {
        Calendar.getInstance()
    }

    val sexes = listOf(
        "Homme",
        "Femme",
        "Autre"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // En-tête
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "Ajouter un habitant",
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "Informations personnelles",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Identité",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = nom,
                    onValueChange = {
                        nom = it
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Nom")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = prenom,
                    onValueChange = {
                        prenom = it
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Prénom")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = sexeMenuOuvert,
                    onExpandedChange = {
                        sexeMenuOuvert = !sexeMenuOuvert
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    OutlinedTextField(
                        value = sexe,
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Wc,
                                contentDescription = null
                            )
                        },
                        label = {
                            Text("Sexe")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = sexeMenuOuvert
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = sexeMenuOuvert,
                        onDismissRequest = {
                            sexeMenuOuvert = false
                        }
                    ) {
                        sexes.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(option)
                                },
                                onClick = {
                                    sexe = option
                                    sexeMenuOuvert = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dateNaissance,
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text("Date de naissance")
                    },
                    trailingIcon = {
                        androidx.compose.material3.IconButton(
                            onClick = {
                                DatePickerDialog(
                                    context,
                                    { _: DatePicker, year: Int, month: Int, day: Int ->
                                        dateNaissance = String.format(
                                            "%02d/%02d/%04d",
                                            day,
                                            month + 1,
                                            year
                                        )
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Choisir la date"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    singleLine = true
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Contact",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }

                CountryCodePickerTextField(
                    number = telephone,
                    onValueChange = { countryCode, number, isValid ->
                        codePaysTelephone = countryCode
                        telephone = number
                        telephoneValide =
                            number.isBlank() || isValid
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = {
                        Text("Téléphone")
                    },
                    showError = telephone.isNotBlank() &&
                            !telephoneValide,
                    showSheet = true
                )

                if (
                    telephone.isNotBlank() &&
                    !telephoneValide
                ) {
                    Text(
                        text = "Numéro de téléphone invalide",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {

                if (
                    telephone.isNotBlank() &&
                    !telephoneValide
                ) {
                    return@Button
                }

                val parts = dateNaissance.split("/")

                val datePourLaBase =
                    if (parts.size == 3) {
                        "${parts[2]}-${parts[1]}-${parts[0]}"
                    } else {
                        dateNaissance
                    }

                viewModel.ajouterHabitant(
                    nom = nom.trim(),
                    prenom = prenom.trim(),
                    sexe = sexe,
                    dateNaissance = datePourLaBase,
                    telephone = telephone.trim().ifBlank {
                        null
                    },
                    codePaysTelephone = codePaysTelephone
                )

                onHabitantAjoute()
            },
            enabled =
                nom.isNotBlank() &&
                        prenom.isNotBlank() &&
                        sexe.isNotBlank() &&
                        dateNaissance.isNotBlank() &&
                        (
                                telephone.isBlank() ||
                                        telephoneValide
                                ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer l'habitant")
        }
    }
}