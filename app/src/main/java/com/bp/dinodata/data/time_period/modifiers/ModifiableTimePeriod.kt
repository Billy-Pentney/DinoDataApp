package com.bp.dinodata.data.time_period.modifiers

import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.ITimeModifierKey

/**
 * Represents a time-period which can be modified, e.g.
 * the Cretaceous epoch can become "Early Cretaceous"
 * */
interface IModifiableTimePeriod: IDisplayableTimePeriod {
    fun with(modifierKey: ITimeModifierKey): IModifiableTimePeriod?
}