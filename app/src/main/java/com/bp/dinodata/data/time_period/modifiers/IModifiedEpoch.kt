package com.bp.dinodata.data.time_period.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.bp.dinodata.data.time_period.EraId
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.IEpoch
import com.bp.dinodata.data.time_period.IEpochId
import com.bp.dinodata.data.time_period.ITimeInterval
import com.bp.dinodata.data.time_period.ITimeModifierKey

interface IModifiedEpoch: IEpoch, IModifiedTimePeriod<IEpoch> {
    override fun getBase(): IEpoch

    override fun getEndTimeInMYA(): Float = getBase().getEndTimeInMYA()
    override fun getStartTimeInMYA(): Float = getBase().getStartTimeInMYA()
    override fun getBrushBrightToDark(): Brush = getBase().getBrushBrightToDark()
    override fun getBrushDarkToBright(): Brush = getBase().getBrushDarkToBright()
    override fun with(modifierKey: ITimeModifierKey): IEpoch? = null
    override fun getEpochId(): IEpochId = getBase().getEpochId()
    override fun getEraId(): EraId = getBase().getEraId()
    override fun getNameResId(): Int = getBase().getNameResId()

    @Composable
    override fun getName(): String = getBase().getName()
}

class ModifiedEpoch(
    private val namePrefix: String,
    private val timeInterval: ITimeInterval,
    private val epoch: IEpoch
): IModifiedEpoch {
    override fun getBase(): IEpoch = epoch
    override fun getStartTimeInMYA(): Float = timeInterval.getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = timeInterval.getEndTimeInMYA()

    @Composable
    override fun getName(): String = namePrefix + " " + epoch.getName()
    override fun getSubdivisions(): List<IDisplayableTimePeriod> {
        return epoch.getSubdivisions().filter {
            timeInterval.contains(it)
        }
    }

    override fun getSubepochs(): List<IEpoch> {
        return listOf(this)
    }
}