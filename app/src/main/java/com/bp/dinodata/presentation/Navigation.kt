package com.bp.dinodata.presentation

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun MyNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screens.ListGenus.route
    ) {
        composable(
            Screens.ListGenus.route,
            enterTransition = { fadeIn() + scaleIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { scaleIn() }
        ) {
            ListGenusScreen(
                listGenusViewModel = hiltViewModel(),
                navigateToGenus = { genus: String ->
                    val route = "${Screens.DetailGenus.route}/${genus}"
                    Log.d("NavHost", "Attempt to navigate to \'$route\'")
                    navController.navigate(route)
                }
            )
        }

        val genusKey = DetailGenusViewModel.GENUS_KEY

        composable(
            Screens.DetailGenus.route + "/{$genusKey}",
            arguments = listOf(navArgument(genusKey){ type = NavType.StringType }),
            enterTransition = { fadeIn() + scaleIn() },
            exitTransition = { fadeOut() + scaleOut() },
            popExitTransition = { fadeOut() + scaleOut() }
        ) {
            DetailGenusScreen(
                detailGenusViewModel = hiltViewModel(),
            )
        }
    }
}