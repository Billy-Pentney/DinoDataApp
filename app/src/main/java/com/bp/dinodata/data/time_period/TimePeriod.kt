package com.bp.dinodata.data.time_period

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

interface INamedTimePeriod {
    fun getNameResId(): Int
}

interface IDisplayableTimePeriod: ITimeInterval, INamedTimePeriod {
    @Composable
    fun getName(): String
    fun getBrushDarkToBright(): Brush
    fun getBrushBrightToDark(): Brush
}

/** Describes a period which cannot be divided into sub-periods. */
interface IAtomicDisplayableTimePeriod: IDisplayableTimePeriod

interface IModified<T: IDisplayableTimePeriod>: IDisplayableTimePeriod {
    fun getUnmodifiedTimePeriod(): T
}

interface IPartitionedTimePeriod {
    fun getSubdivisions(): List<IDisplayableTimePeriod>
}