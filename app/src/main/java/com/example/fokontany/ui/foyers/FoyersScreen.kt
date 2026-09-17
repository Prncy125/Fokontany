package com.example.fokontany.ui.foyers

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fokontany.data.local.entity.FoyerEntity
import com.example.fokontany.data.local.entity.HabitantEntity
import com.example.fokontany.data.local.relation.FoyerAvecHabitants
import com.example.fokontany.data.local.relation.HabitantAvecFoyer

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FoyersScreen(
    viewModel: FoyersViewModel,
    onFoyerClick: (Long) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val foyers by viewModel.foyers.collectAsState()
    val rechercheQuery by viewModel.rechercheQuery.collectAsState()
    val resultatsRecherche by viewModel.resultatsRecherche.collectAsState()
    val resultatsFoyers by viewModel.resultatsFoyers.collectAsState()
    val typeRecherche by viewModel.typeRecherche.collectAsState()

    var adresse by remember { mutableStateOf("") }
    var quartier by remember { mutableStateOf("") }
    var afficherAjout by remember { mutableStateOf(false) }

    val enRecherche = rechercheQuery.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            TopAppBar(
                title = {
                    Column {
                        Text("Foyers", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (foyers.size == 1) "1 foyer" else " foyers",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )

            Surface(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = typeRecherche == TypeRecherche.HABITANT,
                            onClick = { viewModel.onTypeRechercheChange(TypeRecherche.HABITANT) },
                            label = { Text("Habitant") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        FilterChip(
                            selected = typeRecherche == TypeRecherche.FOYER,
                            onClick = { viewModel.onTypeRechercheChange(TypeRecherche.FOYER) },
                            label = { Text("Foyer") },
                            leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    OutlinedTextField(
                        value = rechercheQuery,
                        onValueChange = { viewModel.onRechercheChange(it) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                        trailingIcon = {
                            if (rechercheQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onRechercheChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Effacer")
                                }
                            }
                        },
                        label = { Text(when (typeRecherche) { TypeRecherche.HABITANT -> "Rechercher un habitant"; TypeRecherche.FOYER -> "Rechercher un foyer" }) },
                        placeholder = { Text(when (typeRecherche) { TypeRecherche.HABITANT -> "Nom ou prenom..."; TypeRecherche.FOYER -> "Adresse ou quartier..." }) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    )
                }
            }

            AnimatedVisibility(visible = enRecherche, enter = fadeIn(), exit = fadeOut()) {
                when (typeRecherche) {
                    TypeRecherche.HABITANT -> {
                        if (resultatsRecherche.isEmpty() && rechercheQuery.isNotBlank()) {
                            EmptyRechercheState("Aucun habitant trouve", rechercheQuery, Icons.Default.Person)
                        } else if (resultatsRecherche.isNotEmpty()) {
                            ResultatsList(count = resultatsRecherche.size, countLabel = { if (it == 1) "1 resultat" else " resultats" }) {
                                items(items = resultatsRecherche, key = { it.habitant.id }) { item ->
                                    HabitantResultatCard(habitant = item.habitant, foyer = item.foyer, onClick = { item.foyer?.let { onFoyerClick(it.idLocal) } })
                                }
                            }
                        }
                    }
                    TypeRecherche.FOYER -> {
                        if (resultatsFoyers.isEmpty() && rechercheQuery.isNotBlank()) {
                            EmptyRechercheState("Aucun foyer trouve", rechercheQuery, Icons.Default.Home)
                        } else if (resultatsFoyers.isNotEmpty()) {
                            ResultatsList(count = resultatsFoyers.size, countLabel = { if (it == 1) "1 foyer" else " foyers" }) {
                                items(items = resultatsFoyers, key = { it.foyer.idLocal }) { item ->
                                    FoyerResultatCard(foyerAvecHabitants = item, onClick = { onFoyerClick(item.foyer.idLocal) })
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = !enRecherche, enter = fadeIn(), exit = fadeOut()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (afficherAjout) {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text("Nouveau foyer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    IconButton(onClick = { adresse = ""; quartier = ""; afficherAjout = false }) {
                                        Icon(Icons.Default.Close, contentDescription = "Fermer")
                                    }
                                }
                                OutlinedTextField(value = adresse, onValueChange = { adresse = it }, leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }, label = { Text("Adresse") }, placeholder = { Text("Ex. Rue de la Republique") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                                OutlinedTextField(value = quartier, onValueChange = { quartier = it }, leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }, label = { Text("Quartier") }, placeholder = { Text("Ex. Rose Hill") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                                Button(onClick = { viewModel.ajouterFoyer(adresse = adresse.trim(), quartier = quartier.trim()); adresse = ""; quartier = ""; afficherAjout = false }, enabled = adresse.isNotBlank() && quartier.isNotBlank(), modifier = Modifier.fillMaxWidth().padding(top = 12.dp), shape = MaterialTheme.shapes.medium) {
                                    Icon(Icons.Default.Add, contentDescription = null); Spacer(modifier = Modifier.size(6.dp)); Text("Ajouter")
                                }
                            }
                        }
                    }

                    if (foyers.isEmpty()) {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.large) {
                                    Icon(Icons.Default.HomeWork, contentDescription = null, modifier = Modifier.padding(16.dp).size(48.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Aucun foyer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text("Appuyez sur + pour commencer", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                            }
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(items = foyers, key = { it.idLocal }) { foyer ->
                                FoyerItem(foyer = foyer, onClick = { onFoyerClick(foyer.idLocal) })
                            }
                        }
                    }
                }
            }
        }

        if (!afficherAjout && !enRecherche) {
            FloatingActionButton(onClick = { afficherAjout = true }, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp), containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un foyer")
            }
        }
    }
}

@Composable
private fun ResultatsList(count: Int, countLabel: (Int) -> String, content: LazyListScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(text = countLabel(count), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp, bottom = 8.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp), content = content)
    }
}

@Composable
private fun EmptyRechercheState(message: String, query: String, icon: ImageVector) {
    Box(modifier = Modifier.fillMaxWidth().fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.large) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(16.dp).size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Aucun resultat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(" pour \"\"", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun FoyerItem(foyer: FoyerEntity, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(10.dp).size(24.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(foyer.adresse, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(foyer.quartier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FoyerResultatCard(foyerAvecHabitants: FoyerAvecHabitants, onClick: () -> Unit) {
    val foyer = foyerAvecHabitants.foyer
    val nbHabitants = foyerAvecHabitants.habitants.size
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium) {
                Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(10.dp).size(24.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text(foyer.adresse, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(foyer.quartier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (nbHabitants == 1) "1 habitant" else " habitants", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun HabitantResultatCard(habitant: HabitantEntity, foyer: FoyerEntity?, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().then(if (foyer != null) Modifier.clickable(onClick = onClick) else Modifier), shape = MaterialTheme.shapes.large, elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = MaterialTheme.shapes.medium) {
                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(10.dp).size(24.dp))
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
                Text("${habitant.nom} ${habitant.prenom}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                if (foyer != null) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(foyer.adresse, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(foyer.quartier, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Text("Foyer introuvable", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                }
            }
            if (foyer != null) { Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
    }
}
