package com.bp.dinodata.data.time_period.modifiers

/** Represents a wrapper around a Time-Period that provides additional information. */
interface IModifiedTimePeriod<T: IModifiableTimePeriod<*>> {
    fun getBase(): IModifiableTimePeriod<*>
}