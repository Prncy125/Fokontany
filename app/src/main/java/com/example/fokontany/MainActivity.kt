package com.example.fokontany

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fokontany.data.local.database.AppDatabase
import com.example.fokontany.data.local.repository.FoyerRepository
import com.example.fokontany.ui.foyers.AjouterHabitantScreen
import com.example.fokontany.ui.foyers.FoyerDetailScreen
import com.example.fokontany.ui.foyers.FoyerDetailViewModel
import com.example.fokontany.ui.foyers.FoyersScreen
import com.example.fokontany.ui.foyers.FoyersViewModel
import com.example.fokontany.ui.theme.FokontanyTheme
import com.example.fokontany.data.local.repository.ProgrammeAideRepository
import com.example.fokontany.ui.aides.AidesScreen
import com.example.fokontany.ui.aides.ProgrammesAideViewModel
import com.example.fokontany.ui.aides.AjouterProgrammeAideScreen
import com.example.fokontany.ui.aides.ModifierProgrammeAideScreen

class MainActivity : ComponentActivity() {

    private val foyersViewModel: FoyersViewModel by viewModels {

        val database = AppDatabase.getDatabase(applicationContext)

        val repository = FoyerRepository(
            database.foyerDao(),
            database.habitantDao()
        )

        object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return FoyersViewModel(repository) as T
            }
        }
    }

    private val programmesAideViewModel: ProgrammesAideViewModel by viewModels {

        val database = AppDatabase.getDatabase(applicationContext)

        val repository = ProgrammeAideRepository(
            database.programmeAideDao()
        )

        object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return ProgrammesAideViewModel(repository) as T
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        val repository = FoyerRepository(
            database.foyerDao(),
            database.habitantDao()
        )

        setContent {
            FokontanyTheme {

                FokontanyNavigation(
                    foyersViewModel = foyersViewModel,
                    programmesAideViewModel = programmesAideViewModel,
                    repository = repository
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FokontanyNavigation(
    foyersViewModel: FoyersViewModel,
    programmesAideViewModel: ProgrammesAideViewModel,
    repository: FoyerRepository
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "foyers"
    ) {

        // ---------------------------------------------------------
        // 1. LISTE DES FOYERS
        // ---------------------------------------------------------

        composable("foyers") {

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->

                FoyersScreen(
                    viewModel = foyersViewModel,

                    onFoyerClick = { foyerId ->
                        navController.navigate(
                            "foyer/$foyerId"
                        )
                    },
                    onAidesClick = {
                        navController.navigate("aides")
                    },
                    contentPadding = innerPadding
                )
            }
        }

        // ---------------------------------------------------------
        // 2. PROGRAMMES D'AIDE
        // ---------------------------------------------------------

        composable("aides") {

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->

                AidesScreen(
                    viewModel = programmesAideViewModel,

                    onAjouterProgramme = {
                        navController.navigate("aides/ajouter")
                    },

                    onModifierProgramme = { programme ->
                        navController.navigate(
                            "aides/modifier/${programme.id}"
                        )
                    }
                )
            }
        }
        composable(
            route = "aides/modifier/{programmeId}",
            arguments = listOf(
                navArgument("programmeId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val programmeId =
                backStackEntry.arguments?.getLong("programmeId")
                    ?: return@composable

            ModifierProgrammeAideScreen(
                viewModel = programmesAideViewModel,
                programmeId = programmeId,

                onProgrammeModifie = {
                    navController.popBackStack()
                },

                onRetour = {
                    navController.popBackStack()
                }
            )
        }

        // ---------------------------------------------------------
        // 3. AJOUTER UN PROGRAMME D'AIDE
        // ---------------------------------------------------------

        composable("aides/ajouter") {

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->

                AjouterProgrammeAideScreen(
                    viewModel = programmesAideViewModel,

                    onProgrammeAjoute = {
                        navController.popBackStack()
                    },

                    onRetour = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // ---------------------------------------------------------
        // 2. DETAIL D'UN FOYER
        // ---------------------------------------------------------

        composable(
            route = "foyer/{foyerId}",
            arguments = listOf(
                navArgument("foyerId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val foyerId =
                backStackEntry.arguments?.getLong("foyerId")
                    ?: return@composable

            val viewModel: FoyerDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {

                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {
                        return FoyerDetailViewModel(
                            repository = repository,
                            foyerId = foyerId
                        ) as T
                    }
                }
            )

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->

                FoyerDetailScreen(
                    viewModel = viewModel,

                    contentPadding = innerPadding,

                    onAjouterHabitant = {
                        navController.navigate(
                            "foyer/$foyerId/ajouter-habitant"
                        )
                    },
                    onFoyerDesactive = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // ---------------------------------------------------------
        // 3. AJOUTER UN HABITANT
        // ---------------------------------------------------------

        composable(
            route = "foyer/{foyerId}/ajouter-habitant",
            arguments = listOf(
                navArgument("foyerId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val foyerId =
                backStackEntry.arguments?.getLong("foyerId")
                    ?: return@composable

            val viewModel: FoyerDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {

                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(
                        modelClass: Class<T>
                    ): T {
                        return FoyerDetailViewModel(
                            repository = repository,
                            foyerId = foyerId
                        ) as T
                    }
                }
            )

            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->

                AjouterHabitantScreen(
                    viewModel = viewModel,

                    contentPadding = innerPadding,

                    onHabitantAjoute = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}