package com.example.fokontany.ui.foyers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Wc
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.ccpc.CountryCodePickerTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjouterHabitantScreen(
    viewModel: FoyerDetailViewModel,
    onHabitantAjoute: () -> Unit,
    onRetour: () -> Unit = {}
) {
    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var sexe by remember { mutableStateOf("") }
    var dateNaissance by remember { mutableStateOf("") }
    var afficherCalendrier by remember { mutableStateOf(false) }
    var telephone by remember { mutableStateOf("") }
    var telephoneValide by remember { mutableStateOf(true) }
    var sexeMenuOuvert by remember { mutableStateOf(false) }
    var codePaysTelephone by remember { mutableStateOf("") }

    val sexes = listOf("Homme", "Femme", "Autre")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajouter un habitant",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onRetour) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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
                        modifier = Modifier.padding(12.dp).size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("Ajouter un habitant", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Informations personnelles", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Identite", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

                    OutlinedTextField(
                        value = nom, onValueChange = { nom = it },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Nom") },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = prenom, onValueChange = { prenom = it },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Prenom") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        singleLine = true
                    )

                    ExposedDropdownMenuBox(
                        expanded = sexeMenuOuvert,
                        onExpandedChange = { sexeMenuOuvert = !sexeMenuOuvert },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = sexe, onValueChange = {}, readOnly = true,
                            leadingIcon = { Icon(Icons.Default.Wc, contentDescription = null) },
                            label = { Text("Sexe") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexeMenuOuvert) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            singleLine = true
                        )
                        ExposedDropdownMenu(expanded = sexeMenuOuvert, onDismissRequest = { sexeMenuOuvert = false }) {
                            sexes.forEach { option ->
                                DropdownMenuItem(text = { Text(option) }, onClick = { sexe = option; sexeMenuOuvert = false })
                            }
                        }
                    }

                    OutlinedTextField(
                        value = dateNaissance, onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Date de naissance") },
                        placeholder = { Text("JJ/MM/AAAA") },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { afficherCalendrier = true }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Choisir la date de naissance")
                            }
                        }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Contact", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 10.dp), fontWeight = FontWeight.SemiBold)
                    }

                    CountryCodePickerTextField(
                        number = telephone,
                        onValueChange = { countryCode, number, isValid ->
                            codePaysTelephone = countryCode; telephone = number
                            telephoneValide = number.isBlank() || isValid
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        label = { Text("Telephone") },
                        showError = telephone.isNotBlank() && !telephoneValide,
                        showSheet = true
                    )

                    if (telephone.isNotBlank() && !telephoneValide) {
                        Text("Numero de telephone invalide", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (telephone.isNotBlank() && !telephoneValide) return@Button
                    val parts = dateNaissance.split("/")
                    val datePourLaBase = if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else dateNaissance
                    viewModel.ajouterHabitant(nom = nom.trim(), prenom = prenom.trim(), sexe = sexe, dateNaissance = datePourLaBase, telephone = telephone.trim().ifBlank { null }, codePaysTelephone = codePaysTelephone)
                    onHabitantAjoute()
                },
                enabled = nom.isNotBlank() && prenom.isNotBlank() && sexe.isNotBlank() && dateNaissance.isNotBlank() && (telephone.isBlank() || telephoneValide),
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) { Text("Enregistrer l'habitant") }
        }
    }

    if (afficherCalendrier) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { afficherCalendrier = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val date = Date(millis)
                        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dateNaissance = format.format(date)
                    }
                    afficherCalendrier = false
                }) { Text("Valider") }
            },
            dismissButton = {
                TextButton(onClick = { afficherCalendrier = false }) { Text("Annuler") }
            }
        ) { DatePicker(state = datePickerState) }
    }
}
