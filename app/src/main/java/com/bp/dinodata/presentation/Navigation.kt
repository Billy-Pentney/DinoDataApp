package com.bp.dinodata.presentation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bp.dinodata.presentation.about.AboutScreen
import com.bp.dinodata.presentation.detail_genus.DetailGenusScreen
import com.bp.dinodata.presentation.detail_genus.DetailGenusViewModel
import com.bp.dinodata.presentation.list_genus.ListGenusScreen
import com.bp.dinodata.presentation.taxonomy_screen.TaxonomyScreen
import com.bp.dinodata.presentation.taxonomy_screen.TaxonomyScreenViewModel
import kotlinx.coroutines.launch

@Composable
fun MyNavigation(
    navController: NavHostController = rememberNavController()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val closeDrawer = {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    val optionsSingleTop = NavOptions.Builder().setLaunchSingleTop(true).build()
    var visibleScreen: MutableState<Screen> = remember { mutableStateOf(Screen.ListGenus) }

    MyNavigationDrawer(
        screenState = visibleScreen,
        drawerState = drawerState,
        navigateTo = {
            when (it) {
                Screen.About -> {
                    navController.navigate(
                        Screen.About.route,
                        navOptions = optionsSingleTop
                    )
                    visibleScreen.value = Screen.About
                    closeDrawer()
                }
                Screen.ListGenus -> {
                    navController.navigate(
                        Screen.ListGenus.route,
                        navOptions = optionsSingleTop
                    )
                    visibleScreen.value = Screen.ListGenus
                    closeDrawer()
                }
                Screen.Taxonomy -> {
                    visibleScreen.value = Screen.Taxonomy
                    navController.navigate(Screen.Taxonomy.route)
                    closeDrawer()
                }
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.ListGenus.route
        ) {
            composable(
                Screen.ListGenus.route,
                enterTransition = { fadeIn() + scaleIn(initialScale = 0.5f) + expandIn() },
                exitTransition = { fadeOut() + slideOutOfContainer(towards = AnimatedContentTransitionScope.SlideDirection.Left) },
                popEnterTransition = { fadeIn() + slideIntoContainer(towards = AnimatedContentTransitionScope.SlideDirection.Right) },
                popExitTransition = { fadeOut() }
            ) {
                ListGenusScreen(
                    listGenusViewModel = hiltViewModel(),
                    navigateToGenus = { genus: String ->
                        val route = "${Screen.DetailGenus.route}/${genus}"
                        Log.d("NavHost", "Attempt to navigate to \'$route\'")
                        navController.navigate(route, optionsSingleTop)
                    },
                    openNavDrawer = {
                        coroutineScope.launch { drawerState.open() }
                    }
                )
            }

            val genusKey = DetailGenusViewModel.GENUS_KEY

            composable(
                Screen.DetailGenus.route + "/{$genusKey}",
                arguments = listOf(navArgument(genusKey) { type = NavType.StringType }),
                enterTransition = { fadeIn() + scaleIn() },
                exitTransition = { fadeOut() + shrinkOut() },
                popExitTransition = {
                    fadeOut(
                        animationSpec = tween(200)
                    ) + scaleOut(targetScale = 0.5f)
                },
                popEnterTransition = { fadeIn() }
            ) {
                DetailGenusScreen(
                    detailGenusViewModel = hiltViewModel(),
                )
            }

            composable(Screen.About.route) {
                AboutScreen(
                    navigateBack = {
                        navController.navigateUp()
                    }
                )
            }

            composable(Screen.Taxonomy.route) {
                TaxonomyScreen(
                    hiltViewModel<TaxonomyScreenViewModel>()
                )
            }
        }
    }
}