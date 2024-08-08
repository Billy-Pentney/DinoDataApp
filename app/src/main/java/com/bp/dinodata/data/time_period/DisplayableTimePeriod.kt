package com.bp.dinodata.data.time_period

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

abstract class DisplayableTimePeriod(
    private val internalName: String,
    private val nameResId: Int,
    private val colorLight: Color,
    private val colorDark: Color = colorLight
): IDisplayableTimePeriod {
    override fun toString(): String = internalName

    @Composable
    override fun getName(): String = stringResource(nameResId)
    override fun getNameResId(): Int = nameResId
    override fun getBrushDarkToBright(): Brush = Brush.linearGradient(
        0f to colorDark, 1f to colorLight
    )

    override fun getBrushBrightToDark(): Brush = Brush.linearGradient(
        0f to colorLight, 1f to colorDark
    )

    abstract fun getTimeInterval(): ITimeInterval
    override fun getStartTimeInMYA(): Float = getTimeInterval().getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = getTimeInterval().getEndTimeInMYA()
}