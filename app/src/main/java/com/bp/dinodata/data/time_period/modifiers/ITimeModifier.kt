package com.bp.dinodata.data.time_period.modifiers

import com.bp.dinodata.data.time_period.epochs.IModifiableEpoch
import com.bp.dinodata.data.time_period.intervals.ITimeInterval

interface ITimeModifier {
    fun applyTo(epoch: IModifiableEpoch): IModifiedEpoch
    fun getKey(): ITimeModifierKey
}

/** Represents a modifier which provides a new time-range for the given Time-Period and a
 *  prefix to the name, e.g. "Early Cretaceous". */
class TimeModifier(
    private val key: ITimeModifierKey,
    private val timeInterval: ITimeInterval,
    private val namePrefix: String = key.toString()
): ITimeModifier {
    override fun applyTo(epoch: IModifiableEpoch): IModifiedEpoch =
        ModifiedEpoch(namePrefix, timeInterval, epoch)

    override fun getKey(): ITimeModifierKey = key
}