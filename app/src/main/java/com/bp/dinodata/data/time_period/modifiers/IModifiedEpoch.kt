package com.bp.dinodata.data.time_period.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import com.bp.dinodata.data.time_period.era.EraId
import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.epochs.IEpoch
import com.bp.dinodata.data.time_period.epochs.IEpochId
import com.bp.dinodata.data.time_period.epochs.IModifiableEpoch
import com.bp.dinodata.data.time_period.intervals.ITimeInterval

interface IModifiedEpoch: IEpoch, IModifiedTimePeriod<IModifiableEpoch> {
    override fun getBase(): IModifiableEpoch

    override fun getEndTimeInMYA(): Float = getBase().getEndTimeInMYA()
    override fun getStartTimeInMYA(): Float = getBase().getStartTimeInMYA()
    override fun getBrushBrightToDark(): Brush = getBase().getBrushBrightToDark()
    override fun getBrushDarkToBright(): Brush = getBase().getBrushDarkToBright()
    override fun getEpochId(): IEpochId = getBase().getEpochId()
    override fun getEraId(): EraId = getBase().getEraId()
    override fun getNameResId(): Int = getBase().getNameResId()

    @Composable
    override fun getName(): String = getBase().getName()
}

/**
 * Represents an Epoch with a modifier applied.
 * This modifier modifies the values returned by the epoch to be of a smaller range, without
 * replacing the original values.
 * "Early Cretaceous" is 140-110 mya, while "Cretaceous" is 140-65 mya. */
class ModifiedEpoch(
    private val namePrefix: String,
    private val timeInterval: ITimeInterval,
    private val epoch: IModifiableEpoch
): IModifiedEpoch {
    private val subdivisions = epoch.getSubdivisions().filter {
        timeInterval.contains(it)
    }

    override fun getBase(): IModifiableEpoch = epoch
    override fun getStartTimeInMYA(): Float = timeInterval.getStartTimeInMYA()
    override fun getEndTimeInMYA(): Float = timeInterval.getEndTimeInMYA()

    @Composable
    override fun getName(): String = namePrefix + " " + epoch.getName()
    override fun getSubdivisions(): List<IDisplayableTimePeriod> {
        return subdivisions.ifEmpty {
            listOf(this)
        }
    }

    override fun getSubepochs(): List<IEpoch> {
        return listOf(this)
    }

    // The modified epoch returns the standard epoch as its parent
    override fun getParentPeriod(): IDisplayableTimePeriod = epoch
}