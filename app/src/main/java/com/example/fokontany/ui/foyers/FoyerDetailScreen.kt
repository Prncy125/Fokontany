package com.example.fokontany.ui.foyers

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fokontany.data.local.entity.HabitantEntity
import com.rejowan.ccpc.Country
import com.rejowan.ccpc.CountryCodePickerTextField
import java.util.Calendar

@Composable
fun FoyerDetailScreen(
    viewModel: FoyerDetailViewModel,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onAjouterHabitant: () -> Unit,
    onFoyerDesactive: () -> Unit
) {
    val foyerAvecHabitants by viewModel.foyer.collectAsState()

    val foyerData = foyerAvecHabitants

    if (foyerData == null) {
        Text(
            text = "Foyer introuvable.",
            modifier = Modifier
                .padding(contentPadding)
                .padding(16.dp)
        )
        return
    }

    val foyer = foyerData.foyer
    val habitants = foyerData.habitants

    var modeModification by remember { mutableStateOf(false) }
    var adresse by remember { mutableStateOf(foyer.adresse) }
    var quartier by remember { mutableStateOf(foyer.quartier) }
    var afficherConfirmation by remember { mutableStateOf(false) }

    if (afficherConfirmation) {
        AlertDialog(
            onDismissRequest = {
                afficherConfirmation = false
            },
            title = {
                Text("Désactiver le foyer")
            },
            text = {
                Text(
                    "Voulez-vous vraiment désactiver ce foyer ? " +
                            "Il ne sera plus affiché dans la liste principale."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        afficherConfirmation = false
                        viewModel.desactiverFoyer()
                        onFoyerDesactive()
                    }
                ) {
                    Text("Désactiver")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        afficherConfirmation = false
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
    ) {
        Text(
            text = "Détail du foyer",
            style = MaterialTheme.typography.headlineMedium
        )

        if (modeModification) {
            OutlinedTextField(
                value = adresse,
                onValueChange = { adresse = it },
                label = { Text("Adresse") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = quartier,
                onValueChange = { quartier = it },
                label = { Text("Quartier") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    if (
                        adresse.isNotBlank() &&
                        quartier.isNotBlank()
                    ) {
                        viewModel.modifierFoyer(
                            adresse = adresse,
                            quartier = quartier
                        )
                        modeModification = false
                    }
                },
                enabled = adresse.isNotBlank() && quartier.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Enregistrer les modifications")
            }

            TextButton(
                onClick = {
                    adresse = foyer.adresse
                    quartier = foyer.quartier
                    modeModification = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Annuler")
            }
        } else {
            Text(
                text = "Adresse : ${foyer.adresse}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Quartier : ${foyer.quartier}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Date d'enregistrement : ${foyer.dateEnregistrement}",
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = if (foyer.actif) "Statut : Actif" else "Statut : Inactif",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(
                onClick = {
                    adresse = foyer.adresse
                    quartier = foyer.quartier
                    modeModification = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Modifier le foyer")
            }

            Button(
                onClick = {
                    afficherConfirmation = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Désactiver le foyer")
            }
        }

        Button(
            onClick = onAjouterHabitant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Ajouter un habitant")
        }

        Text(
            text = "Habitants (${habitants.size})",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(
                top = 24.dp,
                bottom = 8.dp
            )
        )

        if (habitants.isEmpty()) {
            Text(
                text = "Aucun habitant enregistré dans ce foyer."
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = habitants,
                    key = { it.id }
                ) { habitant ->
                    HabitantItem(
                        habitant = habitant,
                        onDefinirRepresentant = {
                            viewModel.definirRepresentant(habitant.id)
                        },
                        onModifier = { nom, prenom, sexe, dateNaissance, telephone, codePaysTelephone ->
                            viewModel.modifierHabitant(
                                habitant = habitant,
                                nom = nom,
                                prenom = prenom,
                                sexe = sexe,
                                dateNaissance = dateNaissance,
                                telephone = telephone,
                                codePaysTelephone = codePaysTelephone
                            )
                        },
                        onDesactiver = {
                            viewModel.desactiverHabitant(habitant.id)
                        },
                        onActiver = {
                            viewModel.activerHabitant(habitant.id)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitantItem(
    habitant: HabitantEntity,
    onDefinirRepresentant: () -> Unit,
    onModifier: (String, String, String, String, String?, String) -> Unit,
    onDesactiver: () -> Unit,
    onActiver: () -> Unit
) {
    var modeModification by remember { mutableStateOf(false) }
    var afficherConfirmation by remember { mutableStateOf(false) }

    var nom by remember { mutableStateOf(habitant.nom) }
    var prenom by remember { mutableStateOf(habitant.prenom) }
    var sexe by remember { mutableStateOf(habitant.sexe) }
    var dateNaissance by remember { mutableStateOf(habitant.dateNaissance) }
    var telephone by remember { mutableStateOf(habitant.telephone ?: "") }
    var codePaysTelephone by remember {
        mutableStateOf(habitant.codePaysTelephone)
    }
    val paysTelephone = Country.findCountry(codePaysTelephone)

    val context = LocalContext.current

    var sexeMenuOuvert by remember { mutableStateOf(false) }

    val calendar = remember { Calendar.getInstance() }

    val sexes = listOf("Homme", "Femme", "Autre")

    if (afficherConfirmation) {
        AlertDialog(
            onDismissRequest = {
                afficherConfirmation = false
            },
            title = {
                Text("Désactiver l'habitant")
            },
            text = {
                Text(
                    "Voulez-vous vraiment désactiver " +
                            "${habitant.prenom} ${habitant.nom} ?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        afficherConfirmation = false
                        onDesactiver()
                    }
                ) {
                    Text("Désactiver")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        afficherConfirmation = false
                    }
                ) {
                    Text("Annuler")
                }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (modeModification) {
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = prenom,
                    onValueChange = { prenom = it },
                    label = { Text("Prénom") },
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
                        label = { Text("Sexe") },
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
                                text = { Text(option) },
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
                    label = { Text("Date de naissance") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val parts = dateNaissance.split("-")

                                val year = parts.getOrNull(0)
                                    ?.toIntOrNull()
                                    ?: calendar.get(Calendar.YEAR)

                                val month = (
                                        parts.getOrNull(1)
                                            ?.toIntOrNull()
                                            ?: calendar.get(Calendar.MONTH) + 1
                                        ) - 1

                                val day = parts.getOrNull(2)
                                    ?.toIntOrNull()
                                    ?: calendar.get(Calendar.DAY_OF_MONTH)

                                DatePickerDialog(
                                    context,
                                    { _, selectedYear, selectedMonth, selectedDay ->
                                        dateNaissance = String.format(
                                            "%04d-%02d-%02d",
                                            selectedYear,
                                            selectedMonth + 1,
                                            selectedDay
                                        )
                                    },
                                    year,
                                    month,
                                    day
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Choisir la date"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    singleLine = true
                )

                CountryCodePickerTextField(
                    number = telephone,
                    onValueChange = { countryCode, number, _ ->
                        codePaysTelephone = countryCode
                        telephone = number
                    },
                    selectedCountry = paysTelephone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = { Text("Téléphone") },
                    showSheet = true
                )

                Button(
                    onClick = {
                        if (
                            nom.isNotBlank() &&
                            prenom.isNotBlank() &&
                            sexe.isNotBlank() &&
                            dateNaissance.isNotBlank()
                        ) {
                            onModifier(
                                nom,
                                prenom,
                                sexe,
                                dateNaissance,
                                telephone.trim().ifBlank { null },
                                codePaysTelephone
                            )

                            modeModification = false
                        }
                    },
                    enabled =
                        nom.isNotBlank() &&
                                prenom.isNotBlank() &&
                                sexe.isNotBlank() &&
                                dateNaissance.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Enregistrer")
                }

                TextButton(
                    onClick = {
                        nom = habitant.nom
                        prenom = habitant.prenom
                        sexe = habitant.sexe
                        dateNaissance = habitant.dateNaissance
                        telephone = habitant.telephone ?: ""
                        modeModification = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Annuler")
                }
            } else {
                Text(
                    text = "${habitant.prenom} ${habitant.nom}",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "Sexe : ${habitant.sexe}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Date de naissance : ${habitant.dateNaissance}",
                    style = MaterialTheme.typography.bodyMedium
                )

                if (habitant.telephone != null) {
                    Text(
                        text = "Téléphone : ${habitant.telephone}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (!habitant.actif) {
                    Text(
                        text = "Habitant désactivé",
                        style = MaterialTheme.typography.labelLarge
                    )
                } else if (habitant.estRepresentant) {
                    Text(
                        text = "Représentant actuel",
                        style = MaterialTheme.typography.labelLarge
                    )
                } else {
                    Button(
                        onClick = onDefinirRepresentant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Définir comme représentant")
                    }
                }

                if (habitant.actif) {
                    Button(
                        onClick = {
                            nom = habitant.nom
                            prenom = habitant.prenom
                            sexe = habitant.sexe
                            dateNaissance = habitant.dateNaissance
                            telephone = habitant.telephone ?: ""

                            modeModification = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Modifier l'habitant")
                    }
                }

                if (habitant.actif) {
                    Button(
                        onClick = {
                            afficherConfirmation = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Désactiver l'habitant")
                    }
                } else {
                    Button(
                        onClick = onActiver,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Activer l'habitant")
                    }
                }
            }
        }
    }
}