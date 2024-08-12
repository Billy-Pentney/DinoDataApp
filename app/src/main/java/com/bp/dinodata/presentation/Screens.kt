package com.bp.dinodata.presentation

interface IScreen
interface INavigationDrawerScreen: IScreen

sealed class Screens(
    val route: String
): IScreen {
    data object DetailGenus: Screens("detail")
    data object ListGenus: Screens("list"), INavigationDrawerScreen
    data object Taxonomy: Screens("taxonomy"), INavigationDrawerScreen
    data object About: Screens("about_app"), INavigationDrawerScreen
}

