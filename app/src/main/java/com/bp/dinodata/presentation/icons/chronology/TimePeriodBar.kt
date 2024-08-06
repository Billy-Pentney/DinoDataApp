package com.bp.dinodata.presentation.icons.chronology

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

interface INamedTimePeriod {
    @Composable
    fun getName(): String
}

interface IDisplayableTimePeriod: ITimeInterval, INamedTimePeriod {
    fun getBrushDarkToBright(): Brush
    fun getBrushBrightToDark(): Brush
}