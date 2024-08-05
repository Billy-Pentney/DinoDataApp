package com.bp.dinodata.presentation.icons.chronology

data class TimeInterval(
    private var from: Float,
    private var to: Float
): ITimeInterval {
    init {
        // Swap to enforce that start <= end
        if (from < to) {
            val temp = from
            from = to
            to = temp
        }
    }

    override fun getStartTimeInMYA(): Float = from
    override fun getEndTimeInMYA(): Float = to
    override fun overlapsWith(other: ITimeInterval): Boolean {
        return other.getStartTimeInMYA() >= from
                && to <= other.getEndTimeInMYA()
    }

    override fun toString(): String {
        return "$from-$to mya"
    }
}

interface ITimeInterval {
    fun getStartTimeInMYA(): Float
    fun getEndTimeInMYA(): Float
    fun getDurationInMYA(): Float = getStartTimeInMYA() - getEndTimeInMYA()

    /**
     * Returns true if at least one point is contained in both this interval and
     * the other interval.
     */
    fun overlapsWith(other: ITimeInterval): Boolean {
        val start = this.getStartTimeInMYA()
        val end = this.getEndTimeInMYA()
        val otherStart = other.getStartTimeInMYA()
        val otherEnd = other.getEndTimeInMYA()


        // e.g.
        ///         [ ---- THIS ---- ]
        ///                 [ ---- OTHER ---- ]
        val overlapsOtherStart = (otherStart in end..start)
        // e.g.
        ///                    [ ----- THIS ----- ]
        ///             [ --- OTHER --- ]
        val overlapsOtherEnd = (otherEnd in end..start)

        return overlapsOtherStart || overlapsOtherEnd
    }
}