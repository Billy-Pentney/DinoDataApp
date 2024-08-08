package com.bp.dinodata.data.time_period

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

/** Represents a TimePeriod which can provide its own name. */
interface INamedTimePeriod {
    fun getNameResId(): Int
    @Composable
    fun getName(): String
}

/** Represents a TimePeriod which can be displayed using Brushes. */
interface IDisplayableTimePeriod: ITimeInterval, INamedTimePeriod {
    fun getBrushDarkToBright(): Brush
    fun getBrushBrightToDark(): Brush
}

/**
 * Describes a period which cannot provide information about its own sub-periods.
 * This means that the time-period should be treated as a single unit and cannot be divided
 * further. */
interface IAtomicDisplayableTimePeriod: IDisplayableTimePeriod

/**
 * Describes a time-period which is not atomic and can provide its own subdivisions.
 * For example, an Era can be divided into Epochs; an Epoch can be divided into Stages,
 * but each Stage is atomic.
 * */
interface IPartitionedTimePeriod {
    fun getSubdivisions(): List<IDisplayableTimePeriod>
}