package com.bp.dinodata.presentation.icons.chronology

data class TimeMarker(
    private val mya: Float
): ITimeInterval {
    override fun getStartTimeInMYA(): Float = mya
    override fun getEndTimeInMYA(): Float = mya
    override fun overlapsWith(other: ITimeInterval): Boolean {
        return other.getStartTimeInMYA() <= mya
                && mya <= other.getEndTimeInMYA()
    }
}