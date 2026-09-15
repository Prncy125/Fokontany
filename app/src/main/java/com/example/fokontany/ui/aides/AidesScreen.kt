package com.example.fokontany.ui.aides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fokontany.data.local.entity.ProgrammeAideEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AidesScreen(
    viewModel: ProgrammesAideViewModel,
    onAjouterProgramme: () -> Unit,
    onModifierProgramme: (ProgrammeAideEntity) -> Unit
) {
    val programmes by viewModel.programmes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Programmes d'aide",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAjouterProgramme
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Créer un programme d'aide"
                )
            }
        }
    ) { innerPadding ->

        if (programmes.isEmpty()) {
            EmptyAidesState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = programmes,
                    key = { it.id }
                ) { programme ->

                    ProgrammeAideCard(
                        programme = programme,
                        onModifier = {
                            onModifierProgramme(programme)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgrammeAideCard(
    programme: ProgrammeAideEntity,
    onModifier: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Text(
                    text = programme.nom,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onModifier
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modifier le programme"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.padding(horizontal = 6.dp)
                )

                Text(
                    text = "Début : ${programme.dateDebut}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (!programme.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = programme.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyAidesState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.VolunteerActivism,
            contentDescription = null
        )

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Text(
            text = "Aucun programme d'aide",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Créez un programme d'aide avec le bouton +.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}