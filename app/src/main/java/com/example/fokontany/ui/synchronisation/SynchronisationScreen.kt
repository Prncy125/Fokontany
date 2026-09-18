package com.example.fokontany.ui.synchronisation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SynchronisationScreen(
    viewModel: SyncViewModel
) {
    val pending by viewModel.pending.collectAsState()
    val errors by viewModel.errors.collectAsState()
    val etat by viewModel.etat.collectAsState()
    val message by viewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Synchronisation",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Données en attente : $pending",
            modifier = Modifier.padding(top = 24.dp)
        )

        Text(
            text = "Erreurs : $errors",
            modifier = Modifier.padding(top = 8.dp)
        )

        if (etat == EtatSynchronisation.EN_COURS) {
            CircularProgressIndicator(
                modifier = Modifier.padding(top = 24.dp)
            )
        }

        Button(
            onClick = { viewModel.synchroniser() },
            enabled = etat != EtatSynchronisation.EN_COURS,
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Synchroniser maintenant")
        }

        message?.let {
            Text(
                text = it,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}