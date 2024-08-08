package com.bp.dinodata.data.time_period

/**
 * Represents a discrete point in Time.
 * For practicality, this marker is an interval of width 0, so its start and end times
 * are the same.
 * */
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