package com.bp.dinodata.data.time_period

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

interface IModifiedEpoch: IEpoch {
    fun getUnmodifiedEpoch(): IEpoch
    override fun getEndTimeInMYA(): Float = getUnmodifiedEpoch().getEndTimeInMYA()
    override fun getStartTimeInMYA(): Float = getUnmodifiedEpoch().getStartTimeInMYA()
    override fun getBrushBrightToDark(): Brush = getUnmodifiedEpoch().getBrushBrightToDark()
    override fun getBrushDarkToBright(): Brush = getUnmodifiedEpoch().getBrushDarkToBright()
    override fun with(modifierKey: ITimeModifierKey): IEpoch? = null
    override fun getEpochId(): IEpochId = getUnmodifiedEpoch().getEpochId()
    override fun getEraId(): EraId = getUnmodifiedEpoch().getEraId()
    override fun getNameResId(): Int = getUnmodifiedEpoch().getNameResId()

    @Composable
    override fun getName(): String = getUnmodifiedEpoch().getName()
}

interface ITimeModifierKey

enum class Subepoch: ITimeModifierKey { Early, Middle, Late }

interface ITimeModifier {
    fun applyTo(epoch: IEpoch): IModifiedEpoch
    fun getKey(): ITimeModifierKey
}

class TimeModifier(
    private val key: ITimeModifierKey,
    private val timeInterval: ITimeInterval,
    private val namePrefix: String = key.toString()
): ITimeModifier {
    override fun applyTo(epoch: IEpoch): IModifiedEpoch =
        ModifiedEpoch(namePrefix, timeInterval, epoch)

    override fun getKey(): ITimeModifierKey = key
}


class ModifiedEpoch(
    private val namePrefix: String,
    private val timeInterval: ITimeInterval,
    private val epoch: IEpoch
): IModifiedEpoch {
    override fun getUnmodifiedEpoch(): IEpoch = epoch
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