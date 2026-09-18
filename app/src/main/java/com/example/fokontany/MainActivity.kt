package com.example.fokontany

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fokontany.data.local.database.AppDatabase
import com.example.fokontany.data.local.repository.DistributionAideRepository
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
import com.example.fokontany.ui.distribution.DistributionViewModel
import com.example.fokontany.ui.distribution.DistributionsScreen
import com.example.fokontany.ui.distribution.NouvelleDistributionScreen
import com.example.fokontany.data.remote.FakeRemoteDataSource
import com.example.fokontany.data.sync.SyncManager
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.fokontany.ui.synchronisation.SyncViewModel
import com.example.fokontany.ui.synchronisation.SynchronisationScreen

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Foyers : BottomNavItem(
        route = "foyers",
        label = "Foyers",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    data object Aides : BottomNavItem(
        route = "aides",
        label = "Aides",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    )
    data object Distributions : BottomNavItem(
        route = "distributions",
        label = "Distributions",
        selectedIcon = Icons.Filled.VolunteerActivism,
        unselectedIcon = Icons.Outlined.VolunteerActivism
    )
    data object Synchronisation : BottomNavItem(
        route = "synchronisation",
        label = "Sync",
        selectedIcon = Icons.Filled.Sync,
        unselectedIcon = Icons.Outlined.Sync
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Foyers,
    BottomNavItem.Aides,
    BottomNavItem.Distributions,
    BottomNavItem.Synchronisation
)

class MainActivity : ComponentActivity() {

    private val foyersViewModel: FoyersViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = FoyerRepository(database.foyerDao(), database.habitantDao())
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FoyersViewModel(repository) as T
            }
        }
    }

    private val programmesAideViewModel: ProgrammesAideViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProgrammeAideRepository(database.programmeAideDao())
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProgrammesAideViewModel(repository) as T
            }
        }
    }
    private val distributionViewModel: DistributionViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        val distributionRepo = DistributionAideRepository(database.distributionAideDao())
        val programmeRepo = ProgrammeAideRepository(database.programmeAideDao())
        val foyerRepo = FoyerRepository(database.foyerDao(), database.habitantDao())
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DistributionViewModel(distributionRepo, programmeRepo, foyerRepo) as T
            }
        }
    }

    private val syncViewModel: SyncViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)

        val foyerRepository = FoyerRepository(
            database.foyerDao(),
            database.habitantDao()
        )

        val programmeAideRepository =
            ProgrammeAideRepository(database.programmeAideDao())

        val distributionAideRepository =
            DistributionAideRepository(database.distributionAideDao())

        val remoteDataSource = FakeRemoteDataSource()

        val syncManager = SyncManager(
            remoteDataSource = remoteDataSource,
            foyerRepository = foyerRepository,
            programmeAideRepository = programmeAideRepository,
            distributionAideRepository = distributionAideRepository
        )

        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>
            ): T {
                return SyncViewModel(
                    syncManager = syncManager,
                    foyerRepository = foyerRepository,
                    programmeAideRepository = programmeAideRepository,
                    distributionAideRepository = distributionAideRepository
                ) as T
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

        val programmeAideRepository = ProgrammeAideRepository(
            database.programmeAideDao()
        )

        val distributionRepository = DistributionAideRepository(
            database.distributionAideDao()
        )

        val remoteDataSource = FakeRemoteDataSource()

        val syncManager = SyncManager(
            remoteDataSource = remoteDataSource,
            foyerRepository = repository,
            programmeAideRepository = programmeAideRepository,
            distributionAideRepository = distributionRepository
        )

        setContent {
            FokontanyTheme {
                FokontanyApp(
                    foyersViewModel = foyersViewModel,
                    programmesAideViewModel = programmesAideViewModel,
                    distributionViewModel = distributionViewModel,
                    repository = repository,
                    distributionRepository = distributionRepository,
                    syncManager = syncManager,
                    syncViewModel = syncViewModel
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FokontanyApp(
    foyersViewModel: FoyersViewModel,
    programmesAideViewModel: ProgrammesAideViewModel,
    distributionViewModel: DistributionViewModel,
    repository: FoyerRepository,
    distributionRepository: DistributionAideRepository,
    syncManager: SyncManager,
    syncViewModel: SyncViewModel
) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(text = item.label, style = MaterialTheme.typography.labelSmall)
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "foyers",
            modifier = Modifier.padding(innerPadding)
        ) {

            // ---------------------------------------------------------
            // FOYERS
            // ---------------------------------------------------------
            composable("foyers") {
                FoyersScreen(
                    viewModel = foyersViewModel,
                    onFoyerClick = { foyerId ->
                        navController.navigate("foyer/$foyerId")
                    }
                )
            }

            composable(
                route = "foyer/{foyerId}",
                arguments = listOf(navArgument("foyerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val foyerId = backStackEntry.arguments?.getLong("foyerId") ?: return@composable

                val viewModel: FoyerDetailViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return FoyerDetailViewModel(
                                repository = repository,
                                distributionRepository = distributionRepository,
                                foyerId = foyerId
                            ) as T
                        }
                    }
                )

                FoyerDetailScreen(
                    viewModel = viewModel,
                    onAjouterHabitant = {
                        navController.navigate("foyer/$foyerId/ajouter-habitant")
                    },
                    onFoyerDesactive = { navController.popBackStack() },
                    onRetour = { navController.popBackStack() }
                )
            }

            composable(
                route = "foyer/{foyerId}/ajouter-habitant",
                arguments = listOf(navArgument("foyerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val foyerId = backStackEntry.arguments?.getLong("foyerId") ?: return@composable

                val viewModel: FoyerDetailViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return FoyerDetailViewModel(
                                repository = repository,
                                distributionRepository = distributionRepository,
                                foyerId = foyerId
                            ) as T
                        }
                    }
                )

                AjouterHabitantScreen(
                    viewModel = viewModel,
                    onHabitantAjoute = { navController.popBackStack() },
                    onRetour = { navController.popBackStack() }
                )
            }

            // ---------------------------------------------------------
            // AIDES
            // ---------------------------------------------------------
            composable("aides") {
                AidesScreen(
                    viewModel = programmesAideViewModel,
                    onAjouterProgramme = {
                        navController.navigate("aides/ajouter")
                    },
                    onModifierProgramme = { programme ->
                        navController.navigate("aides/modifier/${programme.id}")
                    }
                )
            }

            composable(
                route = "aides/modifier/{programmeId}",
                arguments = listOf(navArgument("programmeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val programmeId = backStackEntry.arguments?.getLong("programmeId") ?: return@composable
                ModifierProgrammeAideScreen(
                    viewModel = programmesAideViewModel,
                    programmeId = programmeId,
                    onProgrammeModifie = { navController.popBackStack() },
                    onRetour = { navController.popBackStack() }
                )
            }

            composable("aides/ajouter") {
                AjouterProgrammeAideScreen(
                    viewModel = programmesAideViewModel,
                    onProgrammeAjoute = { navController.popBackStack() },
                    onRetour = { navController.popBackStack() }
                )
            }

            // ---------------------------------------------------------
            // DISTRIBUTIONS
            // ---------------------------------------------------------
            composable("distributions") {
                DistributionsScreen(
                    viewModel = distributionViewModel,
                    onNouvelleDistribution = {
                        navController.navigate("distributions/nouvelle")
                    }
                )
            }

            composable("distributions/nouvelle") {
                NouvelleDistributionScreen(
                    viewModel = distributionViewModel,
                    onRetour = { navController.popBackStack() },
                    onSucces = { navController.popBackStack() }
                )
            }

            composable("synchronisation") {
                SynchronisationScreen(
                    viewModel = syncViewModel
                )
            }
        }
    }
}
