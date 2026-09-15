package com.example.fokontany.ui.foyers

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Text(
                text = "Foyer introuvable",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {

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
                            imageVector = Icons.Default.Home,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(32.dp)
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = "Détail du foyer",
                            style = MaterialTheme.typography.headlineMedium
                        )

                        Text(
                            text = "${habitants.size} habitant(s)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            item {

                // Informations du foyer
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
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = "Informations du foyer",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(start = 10.dp)
                            )
                        }

                        if (modeModification) {

                            OutlinedTextField(
                                value = adresse,
                                onValueChange = { adresse = it },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text("Adresse")
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = quartier,
                                onValueChange = { quartier = it },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null
                                    )
                                },
                                label = {
                                    Text("Quartier")
                                },
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
                                            adresse = adresse.trim(),
                                            quartier = quartier.trim()
                                        )

                                        modeModification = false
                                    }
                                },
                                enabled = adresse.isNotBlank() &&
                                        quartier.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp)
                            ) {
                                Text("Enregistrer")
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

                            Row(
                                modifier = Modifier.padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Column(
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Text(
                                        text = "Adresse",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = foyer.adresse,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Column(
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Text(
                                        text = "Quartier",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = foyer.quartier,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Column(
                                    modifier = Modifier.padding(start = 10.dp)
                                ) {
                                    Text(
                                        text = "Enregistré le",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Text(
                                        text = foyer.dateEnregistrement,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            }

                            Surface(
                                color = if (foyer.actif) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier.padding(top = 12.dp)
                            ) {
                                Text(
                                    text = if (foyer.actif) {
                                        "Actif"
                                    } else {
                                        "Inactif"
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (foyer.actif) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        adresse = foyer.adresse
                                        quartier = foyer.quartier
                                        modeModification = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )

                                    Spacer(
                                        modifier = Modifier.size(6.dp)
                                    )

                                    Text("Modifier")
                                }

                                OutlinedButton(
                                    onClick = {
                                        afficherConfirmation = true
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Désactiver")
                                }
                            }
                        }
                    }
                }
            }

            item {

                Button(
                    onClick = onAjouterHabitant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.size(8.dp)
                    )

                    Text("Ajouter un habitant")
                }
            }

            item {

                Text(
                    text = "Habitants",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = if (habitants.size == 1) {
                        "1 personne enregistrée"
                    } else {
                        "${habitants.size} personnes enregistrées"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (habitants.isEmpty()) {

                item {

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.large
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(40.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Text(
                                text = "Aucun habitant",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 12.dp)
                            )

                            Text(
                                text = "Ajoutez les personnes qui vivent dans ce foyer.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

            } else {

                items(
                    items = habitants,
                    key = { it.id }
                ) { habitant ->

                    HabitantItem(
                        habitant = habitant,
                        onDefinirRepresentant = {
                            viewModel.definirRepresentant(habitant.id)
                        },
                        onModifier = {
                                nom,
                                prenom,
                                sexe,
                                dateNaissance,
                                telephone,
                                codePaysTelephone ->
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
    var menuOuvert by remember { mutableStateOf(false) }

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

    val sexes = listOf(
        "Homme",
        "Femme",
        "Autre"
    )

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
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            if (modeModification) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Modifier l'habitant",
                        style = MaterialTheme.typography.titleMedium
                    )

                    IconButton(
                        onClick = {
                            nom = habitant.nom
                            prenom = habitant.prenom
                            sexe = habitant.sexe
                            dateNaissance = habitant.dateNaissance
                            telephone = habitant.telephone ?: ""
                            codePaysTelephone =
                                habitant.codePaysTelephone

                            modeModification = false
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Annuler"
                        )
                    }
                }

                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
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
                    onValueChange = { prenom = it },
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

                    DropdownMenu(
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
                    label = {
                        Text("Date de naissance")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val parts = dateNaissance.split("-")

                                val year = parts
                                    .getOrNull(0)
                                    ?.toIntOrNull()
                                    ?: calendar.get(Calendar.YEAR)

                                val month = (
                                        parts
                                            .getOrNull(1)
                                            ?.toIntOrNull()
                                            ?: calendar.get(Calendar.MONTH) + 1
                                        ) - 1

                                val day = parts
                                    .getOrNull(2)
                                    ?.toIntOrNull()
                                    ?: calendar.get(Calendar.DAY_OF_MONTH)

                                DatePickerDialog(
                                    context,
                                    { _, selectedYear, selectedMonth, selectedDay ->
                                        dateNaissance =
                                            String.format(
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

                CountryCodePickerTextField(
                    number = telephone,
                    onValueChange = {
                            countryCode,
                            number,
                            _ ->
                        codePaysTelephone = countryCode
                        telephone = number
                    },
                    selectedCountry = paysTelephone,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    label = {
                        Text("Téléphone")
                    },
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
                                nom.trim(),
                                prenom.trim(),
                                sexe,
                                dateNaissance,
                                telephone.trim().ifBlank {
                                    null
                                },
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
                        .padding(top = 12.dp)
                ) {
                    Text("Enregistrer")
                }

            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(28.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    ) {
                        Text(
                            text = "${habitant.prenom} ${habitant.nom}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = habitant.sexe,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = {
                            menuOuvert = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Actions"
                        )
                    }

                    DropdownMenu(
                        expanded = menuOuvert,
                        onDismissRequest = {
                            menuOuvert = false
                        }
                    ) {

                        if (habitant.actif && !habitant.estRepresentant) {
                            DropdownMenuItem(
                                text = {
                                    Text("Définir représentant")
                                },
                                onClick = {
                                    menuOuvert = false
                                    onDefinirRepresentant()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null
                                    )
                                }
                            )
                        }

                        if (habitant.actif) {
                            DropdownMenuItem(
                                text = {
                                    Text("Modifier")
                                },
                                onClick = {
                                    menuOuvert = false
                                    modeModification = true
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null
                                    )
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text("Désactiver")
                                },
                                onClick = {
                                    menuOuvert = false
                                    afficherConfirmation = true
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = {
                                    Text("Activer")
                                },
                                onClick = {
                                    menuOuvert = false
                                    onActiver()
                                }
                            )
                        }
                    }
                }

                if (habitant.estRepresentant && habitant.actif) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Text(
                                text = "Représentant",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                    }
                }

                if (!habitant.actif) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text(
                            text = "Désactivé",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Naissance",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = habitant.dateNaissance,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (habitant.telephone != null) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "Téléphone",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Text(
                                    text = habitant.telephone,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}