package com.example.fokontany.ui.foyers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fokontany.data.local.entity.FoyerEntity

@Composable
fun FoyersScreen(
    viewModel: FoyersViewModel,
    onFoyerClick: (Long) -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val foyers by viewModel.foyers.collectAsState()

    var adresse by remember { mutableStateOf("") }
    var quartier by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
    ) {

        Text(
            text = "Foyers",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = adresse,
            onValueChange = { adresse = it },
            label = {
                Text("Adresse")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = quartier,
            onValueChange = { quartier = it },
            label = {
                Text("Quartier")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                viewModel.ajouterFoyer(
                    adresse = adresse,
                    quartier = quartier
                )

                adresse = ""
                quartier = ""
            },
            enabled = adresse.isNotBlank() && quartier.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Ajouter le foyer")
        }

        if (foyers.isEmpty()) {

            Text(
                text = "Aucun foyer enregistré.",
                modifier = Modifier.padding(top = 16.dp)
            )

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                items(
                    items = foyers,
                    key = { it.idLocal }
                ) { foyer ->

                    FoyerItem(
                        foyer = foyer,
                        onClick = {
                            onFoyerClick(foyer.idLocal)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FoyerItem(
    foyer: FoyerEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = foyer.adresse,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = foyer.quartier,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}