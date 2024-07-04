package com.bp.dinodata.presentation

sealed class Screens(
    val route: String
) {
    data object DetailGenus: Screens("detail")
    data object ListGenus: Screens("list")
}