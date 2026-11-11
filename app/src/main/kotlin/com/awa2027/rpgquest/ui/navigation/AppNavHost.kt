package com.awa2027.rpgquest.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.awa2027.rpgquest.ui.board.QuestBoardScreen
import com.awa2027.rpgquest.ui.stats.StatsScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = QuestBoardRoute) {
        composable<QuestBoardRoute> {
            QuestBoardScreen(
                viewModel = koinViewModel(),
                // Taller, paso 6: navega a DiceArenaRoute(questId) y registra ese destino abajo.
                onOpenArena = { }
            )
        }
        // Ya viene hecho: el destino de estadísticas. El questId llega desde la ruta con parametersOf.
        composable<StatsRoute> { entry ->
            val route = entry.toRoute<StatsRoute>()
            StatsScreen(
                viewModel = koinViewModel { parametersOf(route.questId) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
