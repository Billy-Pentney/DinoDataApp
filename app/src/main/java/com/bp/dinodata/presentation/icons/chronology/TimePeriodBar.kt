package com.bp.dinodata.presentation.icons.chronology

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

interface ITimePeriodBar: ITimeInterval {
    fun getBrush(): Brush
    @Composable
    fun getName(): String
}

data class TimePeriodBar(
    private val timePeriodRange: ITimeInterval,
    private val nameResId: Int,
    private val colorLight: Color,
    private val colorDark: Color = colorLight
): ITimePeriodBar {
    private val brush: Brush = Brush.linearGradient(
        0.0f to colorDark,
        1.0f to colorLight
    )

    override fun getStartTimeInMYA(): Float = timePeriodRange.getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = timePeriodRange.getEndTimeInMYA()
    override fun overlapsWith(other: ITimeInterval): Boolean =
        timePeriodRange.overlapsWith(other)

    override fun getBrush(): Brush = brush
    @Composable
    override fun getName(): String = stringResource(id = nameResId)
}