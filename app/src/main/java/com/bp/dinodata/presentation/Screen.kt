package com.bp.dinodata.presentation

interface IScreen
interface INavigationDrawerScreen: IScreen

sealed class Screen(
    val route: String
): IScreen {
    data object DetailGenus: Screen("detail")
    data object ListGenus: Screen("list"), INavigationDrawerScreen
    data object Taxonomy: Screen("taxonomy"), INavigationDrawerScreen
    data object About: Screen("about_app"), INavigationDrawerScreen
}

