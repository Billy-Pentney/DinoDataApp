package com.bp.dinodata.data.time_period.modifiers

import com.bp.dinodata.data.time_period.IEpoch
import com.bp.dinodata.data.time_period.ITimeInterval
import com.bp.dinodata.data.time_period.ITimeModifierKey

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