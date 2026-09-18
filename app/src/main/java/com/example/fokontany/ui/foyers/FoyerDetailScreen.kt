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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fokontany.data.local.entity.DistributionAideEntity
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.domain.model.StatutDistribution
import com.rejowan.ccpc.Country
import com.rejowan.ccpc.CountryCodePickerTextField
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoyerDetailScreen(
    viewModel: FoyerDetailViewModel,
    onAjouterHabitant: () -> Unit,
    onFoyerDesactive: () -> Unit,
    onRetour: () -> Unit = {}
) {
    val foyerAvecHabitants by viewModel.foyer.collectAsState()
    val historiqueDistributions by viewModel.historiqueDistributions.collectAsState()

    val foyerData = foyerAvecHabitants

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail du foyer",
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

        if (foyerData == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
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

            return@Scaffold
        }

        val foyer = foyerData.foyer
        val habitants = foyerData.habitants

        var modeModification by remember { mutableStateOf(false) }
        var adresse by remember { mutableStateOf(foyer.adresse) }
        var quartier by remember { mutableStateOf(foyer.quartier) }
        var afficherConfirmation by remember { mutableStateOf(false) }

        if (afficherConfirmation) {
            AlertDialog(
                onDismissRequest = { afficherConfirmation = false },
                title = { Text("Desactiver le foyer") },
                text = {
                    Text(
                        "Voulez-vous vraiment desactiver ce foyer ? " +
                                "Il ne sera plus affiche dans la liste principale."
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            afficherConfirmation = false
                            viewModel.desactiverFoyer()
                            onFoyerDesactive()
                        }
                    ) { Text("Desactiver") }
                },
                dismissButton = {
                    TextButton(onClick = { afficherConfirmation = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                text = "Detail du foyer",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Informations du foyer",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(start = 10.dp)
                                )
                            }

                            if (modeModification) {
                                OutlinedTextField(
                                    value = adresse,
                                    onValueChange = { adresse = it },
                                    leadingIcon = {
                                        Icon(Icons.Default.Home, contentDescription = null)
                                    },
                                    label = { Text("Adresse") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = quartier,
                                    onValueChange = { quartier = it },
                                    leadingIcon = {
                                        Icon(Icons.Default.LocationOn, contentDescription = null)
                                    },
                                    label = { Text("Quartier") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    singleLine = true
                                )

                                Button(
                                    onClick = {
                                        if (adresse.isNotBlank() && quartier.isNotBlank()) {
                                            viewModel.modifierFoyer(
                                                adresse = adresse.trim(),
                                                quartier = quartier.trim()
                                            )
                                            modeModification = false
                                        }
                                    },
                                    enabled = adresse.isNotBlank() && quartier.isNotBlank(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    shape = MaterialTheme.shapes.medium
                                ) { Text("Enregistrer") }

                                TextButton(
                                    onClick = {
                                        adresse = foyer.adresse
                                        quartier = foyer.quartier
                                        modeModification = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) { Text("Annuler") }
                            } else {
                                Row(
                                    modifier = Modifier.padding(top = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Home, contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Column(modifier = Modifier.padding(start = 10.dp)) {
                                        Text("Adresse", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(foyer.adresse, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }

                                Row(
                                    modifier = Modifier.padding(top = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn, contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Column(modifier = Modifier.padding(start = 10.dp)) {
                                        Text("Quartier", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(foyer.quartier, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }

                                Row(
                                    modifier = Modifier.padding(top = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CalendarMonth, contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Column(modifier = Modifier.padding(start = 10.dp)) {
                                        Text("Enregistre le", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Text(foyer.dateEnregistrement, style = MaterialTheme.typography.bodyLarge)
                                    }
                                }

                                Surface(
                                    color = if (foyer.actif) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.small,
                                    modifier = Modifier.padding(top = 12.dp)
                                ) {
                                    Text(
                                        text = if (foyer.actif) "Actif" else "Inactif",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (foyer.actif) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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
                                        Icon(Icons.Default.Edit, contentDescription = null)
                                        Spacer(modifier = Modifier.size(6.dp))
                                        Text("Modifier")
                                    }

                                    OutlinedButton(
                                        onClick = { afficherConfirmation = true },
                                        modifier = Modifier.weight(1f)
                                    ) { Text("Desactiver") }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = onAjouterHabitant,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Ajouter un habitant")
                    }
                }

                item {
                    Text(
                        text = "Habitants",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (habitants.size == 1) "1 personne enregistree" else "${habitants.size} personnes enregistrees",
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
                                        Icons.Default.Person, contentDescription = null,
                                        modifier = Modifier.padding(16.dp).size(40.dp),
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Text("Aucun habitant", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
                                Text(
                                    "Ajoutez les personnes qui vivent dans ce foyer.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(items = habitants, key = { "hab_${it.id}" }) { habitant ->
                        HabitantItem(
                            habitant = habitant,
                            onDefinirRepresentant = { viewModel.definirRepresentant(habitant.id) },
                            onModifier = { nom, prenom, sexe, dateNaissance, telephone, codePaysTelephone ->
                                viewModel.modifierHabitant(
                                    habitant = habitant,
                                    nom = nom, prenom = prenom, sexe = sexe,
                                    dateNaissance = dateNaissance, telephone = telephone,
                                    codePaysTelephone = codePaysTelephone
                                )
                            },
                            onDesactiver = { viewModel.desactiverHabitant(habitant.id) },
                            onActiver = { viewModel.activerHabitant(habitant.id) }
                        )
                    }
                }

                // ---------------------------------------------------------
                // HISTORIQUE DES AIDES
                // ---------------------------------------------------------

                item {
                    Text(
                        text = "Historique des aides",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    Text(
                        text = if (historiqueDistributions.size == 1) "1 aide recue" else "${historiqueDistributions.size} aides recues",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (historiqueDistributions.isEmpty()) {
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
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = MaterialTheme.shapes.large
                                ) {
                                    Icon(
                                        Icons.Default.VolunteerActivism, contentDescription = null,
                                        modifier = Modifier.padding(16.dp).size(40.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text("Aucune aide recue", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
                                Text(
                                    "Ce foyer n'a pas encore beneficie d'aide.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(items = historiqueDistributions, key = { "dist_${it.id}" }) { distribution ->
                        DistributionItem(distribution = distribution)
                    }
                }
            }
        }
    }
}

@Composable
private fun DistributionItem(distribution: DistributionAideEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (distribution.statut == StatutDistribution.RECUPEREE)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(24.dp),
                    tint = if (distribution.statut == StatutDistribution.RECUPEREE)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = distribution.dateDistribution,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (distribution.statut == StatutDistribution.RECUPEREE)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = if (distribution.statut == StatutDistribution.RECUPEREE) "Recuperee" else "Annulee",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (distribution.statut == StatutDistribution.RECUPEREE)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (distribution.montant != null) {
                        Text(
                            text = "${distribution.montant} Ar",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (distribution.quantite != null) {
                        Text(
                            text = "Qte: ${distribution.quantite}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
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
    var codePaysTelephone by remember { mutableStateOf(habitant.codePaysTelephone) }

    val paysTelephone = Country.findCountry(codePaysTelephone)
    val context = LocalContext.current
    var sexeMenuOuvert by remember { mutableStateOf(false) }
    val calendar = remember { Calendar.getInstance() }
    val sexes = listOf("Homme", "Femme", "Autre")

    if (afficherConfirmation) {
        AlertDialog(
            onDismissRequest = { afficherConfirmation = false },
            title = { Text("Desactiver l'habitant") },
            text = { Text("Voulez-vous vraiment desactiver ${habitant.prenom} ${habitant.nom} ?") },
            confirmButton = {
                TextButton(onClick = { afficherConfirmation = false; onDesactiver() }) { Text("Desactiver") }
            },
            dismissButton = {
                TextButton(onClick = { afficherConfirmation = false }) { Text("Annuler") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (modeModification) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Modifier l'habitant", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    IconButton(onClick = {
                        nom = habitant.nom; prenom = habitant.prenom; sexe = habitant.sexe
                        dateNaissance = habitant.dateNaissance; telephone = habitant.telephone ?: ""
                        codePaysTelephone = habitant.codePaysTelephone; modeModification = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Annuler")
                    }
                }

                OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom") }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true)
                OutlinedTextField(value = prenom, onValueChange = { prenom = it }, label = { Text("Prenom") }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true)

                ExposedDropdownMenuBox(expanded = sexeMenuOuvert, onExpandedChange = { sexeMenuOuvert = !sexeMenuOuvert }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = sexe, onValueChange = {}, readOnly = true, label = { Text("Sexe") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexeMenuOuvert) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(), singleLine = true
                    )
                    DropdownMenu(expanded = sexeMenuOuvert, onDismissRequest = { sexeMenuOuvert = false }) {
                        sexes.forEach { option ->
                            DropdownMenuItem(text = { Text(option) }, onClick = { sexe = option; sexeMenuOuvert = false })
                        }
                    }
                }

                OutlinedTextField(
                    value = dateNaissance, onValueChange = {}, readOnly = true, label = { Text("Date de naissance") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val parts = dateNaissance.split("-")
                            val year = parts.getOrNull(0)?.toIntOrNull() ?: calendar.get(Calendar.YEAR)
                            val month = (parts.getOrNull(1)?.toIntOrNull() ?: calendar.get(Calendar.MONTH) + 1) - 1
                            val day = parts.getOrNull(2)?.toIntOrNull() ?: calendar.get(Calendar.DAY_OF_MONTH)
                            DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                                dateNaissance = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                            }, year, month, day).show()
                        }) { Icon(Icons.Default.CalendarMonth, contentDescription = "Choisir la date") }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true
                )

                CountryCodePickerTextField(
                    number = telephone, onValueChange = { countryCode, number, _ -> codePaysTelephone = countryCode; telephone = number },
                    selectedCountry = paysTelephone, modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    label = { Text("Telephone") }, showSheet = true
                )

                Button(
                    onClick = {
                        if (nom.isNotBlank() && prenom.isNotBlank() && sexe.isNotBlank() && dateNaissance.isNotBlank()) {
                            onModifier(nom.trim(), prenom.trim(), sexe, dateNaissance, telephone.trim().ifBlank { null }, codePaysTelephone)
                            modeModification = false
                        }
                    },
                    enabled = nom.isNotBlank() && prenom.isNotBlank() && sexe.isNotBlank() && dateNaissance.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    shape = MaterialTheme.shapes.medium
                ) { Text("Enregistrer") }

            } else {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(10.dp).size(28.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("${habitant.prenom} ${habitant.nom}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(habitant.sexe, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { menuOuvert = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Actions")
                    }
                    DropdownMenu(expanded = menuOuvert, onDismissRequest = { menuOuvert = false }) {
                        if (habitant.actif && !habitant.estRepresentant) {
                            DropdownMenuItem(text = { Text("Definir representant") }, onClick = { menuOuvert = false; onDefinirRepresentant() }, leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) })
                        }
                        if (habitant.actif) {
                            DropdownMenuItem(text = { Text("Modifier") }, onClick = { menuOuvert = false; modeModification = true }, leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) })
                            DropdownMenuItem(text = { Text("Desactiver") }, onClick = { menuOuvert = false; afficherConfirmation = true })
                        } else {
                            DropdownMenuItem(text = { Text("Activer") }, onClick = { menuOuvert = false; onActiver() })
                        }
                    }
                }

                if (habitant.estRepresentant && habitant.actif) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.small, modifier = Modifier.padding(top = 12.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("Representant", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                }

                if (!habitant.actif) {
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small, modifier = Modifier.padding(top = 12.dp)) {
                        Text("Desactive", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Naissance", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(habitant.dateNaissance, style = MaterialTheme.typography.bodyMedium)
                    }
                    if (habitant.telephone != null) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Telephone", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(habitant.telephone, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
