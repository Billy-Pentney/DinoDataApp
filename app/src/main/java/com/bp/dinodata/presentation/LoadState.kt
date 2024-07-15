package com.bp.dinodata.presentation

sealed class LoadState {
    data object NotLoading: LoadState()
    data object InProgress: LoadState()
    data object Loaded: LoadState()
    data class Error(val reason: String? = null): LoadState()
}
