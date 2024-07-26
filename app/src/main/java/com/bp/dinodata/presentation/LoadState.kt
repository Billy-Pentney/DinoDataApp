package com.bp.dinodata.presentation

import com.bp.dinodata.presentation.DataState.Failed
import com.bp.dinodata.presentation.DataState.Idle
import com.bp.dinodata.presentation.DataState.LoadInProgress
import com.bp.dinodata.presentation.DataState.Success

sealed class DataState<T> {
    class Idle<T>: DataState<T>()
    class LoadInProgress<T>: DataState<T>()
    data class Success<T>(val data: T): DataState<T>()
    data class Failed<T>(val reason: String): DataState<T>()
}

fun<T,R> DataState<T>.map(mappingFunc: (T) -> R): DataState<out R> {
    return when (this) {
        is Failed -> Failed(this.reason)
        is Idle -> Idle()
        is LoadInProgress -> LoadInProgress()
        is Success -> { Success(mappingFunc(this.data)) }
    }
}