package com.example.fokontany.ui.distribution

import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.data.local.entity.ProgrammeAideEntity
import java.util.Calendar
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NouvelleDistributionScreen(
    viewModel: DistributionViewModel,
    onRetour: () -> Unit,
    onSucces: () -> Unit
) {
    val etape by viewModel.etape.collectAsState()
    val programmes by viewModel.programmes.collectAsState()
    val foyers by viewModel.foyers.collectAsState()
    val programmeSelectionne by viewModel.programmeSelectionne.collectAsState()
    val foyerSelectionne by viewModel.foyerSelectionne.collectAsState()
    val representant by viewModel.representant.collectAsState()
    val resultat by viewModel.resultat.collectAsState()

    LaunchedEffect(resultat) {
        if (resultat is EnregistrementResultat.Succes) {
            viewModel.reinitialiser()
            onSucces()
        }
    }

    resultat?.let { res ->
        if (res is EnregistrementResultat.DoubleDistribution) {
            AlertDialog(
                onDismissRequest = { viewModel.onResultatConsomme() },
                title = { Text("Distribution impossible") },
                text = { Text(res.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.onResultatConsomme() }) {
                        Text("OK")
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Nouvelle distribution",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (etape == EtapeDistribution.PROGRAMME) {
                            onRetour()
                        } else {
                            viewModel.onRetour()
                        }
                    }) {
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
        ) {
            // Indicateur d'etapes
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EtapeIndicator(label = "Programme", active = true, completed = etape != EtapeDistribution.PROGRAMME)
                Spacer(modifier = Modifier.width(8.dp))
                EtapeSeparator(active = etape != EtapeDistribution.PROGRAMME)
                Spacer(modifier = Modifier.width(8.dp))
                EtapeIndicator(label = "Foyer", active = etape != EtapeDistribution.PROGRAMME, completed = etape == EtapeDistribution.DETAILS)
                Spacer(modifier = Modifier.width(8.dp))
                EtapeSeparator(active = etape == EtapeDistribution.DETAILS)
                Spacer(modifier = Modifier.width(8.dp))
                EtapeIndicator(label = "Details", active = etape == EtapeDistribution.DETAILS, completed = false)
            }

            AnimatedContent(
                targetState = etape,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f)
            ) { currentEtape ->
                when (currentEtape) {
                    EtapeDistribution.PROGRAMME -> {
                        EtapeProgramme(
                            programmes = programmes,
                            onProgrammeClick = { viewModel.onProgrammeSelectionne(it) }
                        )
                    }
                    EtapeDistribution.FOYER -> {
                        EtapeFoyer(
                            foyers = foyers,
                            onFoyerClick = { viewModel.onFoyerSelectionne(it) }
                        )
                    }
                    EtapeDistribution.DETAILS -> {
                        EtapeDetails(
                            programme = programmeSelectionne,
                            foyer = foyerSelectionne,
                            representant = representant,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EtapeIndicator(label: String, active: Boolean, completed: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            color = when {
                completed -> MaterialTheme.colorScheme.primary
                active -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            },
            shape = MaterialTheme.shapes.small
        ) {
            if (completed) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp).size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = when (label) {
                        "Programme" -> "1"
                        "Foyer" -> "2"
                        else -> "3"
                    },
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EtapeSeparator(active: Boolean) {
    Surface(
        color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .width(24.dp)
            .height(2.dp)
    ) {}
}

@Composable
private fun EtapeProgramme(
    programmes: List<ProgrammeAideEntity>,
    onProgrammeClick: (ProgrammeAideEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Choisir un programme d'aide",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (programmes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(
                            Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            modifier = Modifier.padding(16.dp).size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aucun programme disponible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = programmes, key = { it.id }) { programme ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onProgrammeClick(programme) },
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                                Icon(
                                    Icons.Default.VolunteerActivism,
                                    contentDescription = null,
                                    modifier = Modifier.padding(10.dp).size(24.dp),
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                                Text(programme.nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("Debut : ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                                if (!programme.description.isNullOrBlank()) {
                                    Text(programme.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp), maxLines = 2)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EtapeFoyer(
    foyers: List<FoyerEntity>,
    onFoyerClick: (FoyerEntity) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Choisir un foyer beneficiaire",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        if (foyers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.large) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.padding(16.dp).size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Aucun foyer disponible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(items = foyers, key = { it.idLocal }) { foyer ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onFoyerClick(foyer) },
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                                Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.padding(10.dp).size(24.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                                Text(foyer.adresse, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(foyer.quartier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EtapeDetails(
    programme: ProgrammeAideEntity?,
    foyer: FoyerEntity?,
    representant: HabitantEntity?,
    viewModel: DistributionViewModel
) {
    val dateDistribution by viewModel.dateDistribution.collectAsState()
    val montant by viewModel.montant.collectAsState()
    val quantite by viewModel.quantite.collectAsState()
    val typeAide by viewModel.typeAide.collectAsState()

    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    var afficherCalendrier by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Programme selectionne
        if (programme != null) {
            Card(shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                        Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("Programme", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(programme.nom, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Foyer selectionne
        if (foyer != null) {
            Card(shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text("Foyer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(foyer.adresse, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(foyer.quartier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Representant
        Card(shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp).size(20.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text("Representant", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (representant != null) {
                        Text("${representant.nom} ${representant.prenom}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    } else {
                        Text("Aucun representant", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Type d'aide
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = typeAide == TypeAide.MONTANT,
                onClick = { viewModel.onTypeAideChange(TypeAide.MONTANT) },
                label = { Text("Montant") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            FilterChip(
                selected = typeAide == TypeAide.QUANTITE,
                onClick = { viewModel.onTypeAideChange(TypeAide.QUANTITE) },
                label = { Text("Quantite") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        // Montant ou quantite
        when (typeAide) {
            TypeAide.MONTANT -> {
                OutlinedTextField(
                    value = montant,
                    onValueChange = { viewModel.onMontantChange(it) },
                    label = { Text("Montant (Ar)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            TypeAide.QUANTITE -> {
                OutlinedTextField(
                    value = quantite,
                    onValueChange = { viewModel.onQuantiteChange(it) },
                    label = { Text("Quantite") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Date
        OutlinedTextField(
            value = dateDistribution,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date de distribution") },
            trailingIcon = {
                IconButton(onClick = { afficherCalendrier = true }) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Choisir la date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bouton enregistrer
        val peuxEnregistrer = representant != null
                && dateDistribution.isNotBlank()
                && ((typeAide == TypeAide.MONTANT && montant.isNotBlank()) || (typeAide == TypeAide.QUANTITE && quantite.isNotBlank()))

        Button(
            onClick = { viewModel.enregistrer() },
            enabled = peuxEnregistrer,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Enregistrer la distribution")
        }

        Spacer(modifier = Modifier.height(8.dp))
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
                        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        viewModel.onDateChange(format.format(date))
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