package com.example.fokontany.ui.foyers

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    // ---------------------------------------------------------
    // ETATS
    // ---------------------------------------------------------

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

    // ---------------------------------------------------------
    // CONTEXTE
    // ---------------------------------------------------------

    val context = LocalContext.current

    val calendar = remember {
        Calendar.getInstance()
    }

    var codePaysTelephone by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // LISTE DES SEXES
    // ---------------------------------------------------------

    val sexes = listOf(
        "Homme",
        "Femme",
        "Autre"
    )

    // ---------------------------------------------------------
    // INTERFACE
    // ---------------------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // -----------------------------------------------------
        // TITRE
        // -----------------------------------------------------

        Text(
            text = "Ajouter un habitant"
        )

        // -----------------------------------------------------
        // NOM
        // -----------------------------------------------------

        OutlinedTextField(
            value = nom,
            onValueChange = {
                nom = it
            },
            label = {
                Text("Nom")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // -----------------------------------------------------
        // PRENOM
        // -----------------------------------------------------

        OutlinedTextField(
            value = prenom,
            onValueChange = {
                prenom = it
            },
            label = {
                Text("Prénom")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // -----------------------------------------------------
        // SEXE
        // -----------------------------------------------------

        ExposedDropdownMenuBox(
            expanded = sexeMenuOuvert,
            onExpandedChange = {
                sexeMenuOuvert = !sexeMenuOuvert
            },
            modifier = Modifier.fillMaxWidth()
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

        // -----------------------------------------------------
        // DATE DE NAISSANCE
        // -----------------------------------------------------

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
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Choisir la date de naissance"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // -----------------------------------------------------
        // TELEPHONE
        // -----------------------------------------------------

        CountryCodePickerTextField(
            number = telephone,
            onValueChange = { countryCode, number, isValid ->
                codePaysTelephone = countryCode
                telephone = number
                telephoneValide = number.isBlank() || isValid
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Téléphone") },
            showError = telephone.isNotBlank() && !telephoneValide,
            showSheet = true
        )

        // -----------------------------------------------------
        // ERREUR TELEPHONE
        // -----------------------------------------------------

        if (
            telephone.isNotBlank() &&
            !telephoneValide
        ) {
            Text(
                text = "Numéro de téléphone invalide"
            )
        }

        // -----------------------------------------------------
        // ENREGISTRER
        // -----------------------------------------------------

        Button(
            onClick = {

                if (
                    telephone.isNotBlank() &&
                    !telephoneValide
                ) {
                    return@Button
                }

                // -------------------------------------------------
                // Conversion de la date
                // DD/MM/YYYY -> YYYY-MM-DD
                // -------------------------------------------------

                val parts = dateNaissance.split("/")

                val datePourLaBase =
                    if (parts.size == 3) {
                        "${parts[2]}-${parts[1]}-${parts[0]}"
                    } else {
                        dateNaissance
                    }

                // -------------------------------------------------
                // Enregistrement
                // -------------------------------------------------

                viewModel.ajouterHabitant(
                    nom = nom.trim(),
                    prenom = prenom.trim(),
                    sexe = sexe,
                    dateNaissance = datePourLaBase,
                    telephone = telephone.trim().ifBlank { null },
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

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Enregistrer")
        }
    }
}