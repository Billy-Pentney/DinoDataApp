package com.bp.dinodata.presentation.icons.chronology

interface ITimeMarkerRange {
    fun getStartTimeInMYA(): Float
    fun getEndTimeInMYA(): Float
    fun getDurationInMYA(): Float = getStartTimeInMYA() - getEndTimeInMYA()
    fun overlapsWith(other: ITimeMarkerRange): Boolean
}