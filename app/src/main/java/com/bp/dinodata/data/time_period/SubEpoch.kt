package com.bp.dinodata.data.time_period


/**
 * Represents a type of object which identifies a Modifier.
 * Each instance of this class should be unique.
 * */
interface ITimeModifierKey

enum class SubEpochModifier: ITimeModifierKey { Early, Middle, Late }


