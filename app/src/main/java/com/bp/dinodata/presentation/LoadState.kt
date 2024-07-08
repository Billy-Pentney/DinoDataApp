package com.bp.dinodata.presentation

sealed class LoadState {
    data object NotLoading: LoadState()
    data class LoadingPage(val pageNum: Int): LoadState()
    data class IsLoaded(val pageNum: Int): LoadState()
    data class Error(val reason: String? = null): LoadState()
}
