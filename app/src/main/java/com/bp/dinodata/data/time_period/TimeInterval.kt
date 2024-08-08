package com.bp.dinodata.data.time_period

/**
 * Represents an interval of time in the units of Millions-of-Years-Ago (MYA).
 * Note: this class enforces that from must be no smaller than to; if arguments are
 * provided in ascending order, then they will be flipped.
 * @property from The beginning of the interval, in MYA.
 * @property to The end of the interval, in MYA; must be no greater than from, otherwise
 * arguments will be swapped.
 */
data class TimeInterval(
    private var from: Float,
    private var to: Float
): ITimeInterval {
    init {
        // Swap to enforce that start >= end
        if (from < to) {
            val temp = from
            from = to
            to = temp
        }
    }

    override fun getStartTimeInMYA(): Float = from
    override fun getEndTimeInMYA(): Float = to

    override fun toString(): String {
        return "$from-$to mya"
    }

    override fun equals(other: Any?): Boolean {
        if (other is ITimeInterval) {
            return other.getStartTimeInMYA() == from && other.getEndTimeInMYA() == to
        }
        return false
    }

    /** Auto-generated */
    override fun hashCode(): Int {
        var result = from.hashCode()
        result = 31 * result + to.hashCode()
        return result
    }

    companion object {
        /**
         * Build a new time-interval which encompasses the contents of the given list.
         * The returned TimeInterval is the smallest of those ranges which
         * includes every interval in the input.
         * If the input list is empty, then return an empty interval from 0 to 0.
         */
        fun fromList(interval: List<ITimeInterval>): ITimeInterval {
            val timeIntervalStart = interval.maxOfOrNull{ it.getStartTimeInMYA() } ?: 0f
            val timeIntervalEnd = interval.minOfOrNull { it.getEndTimeInMYA() } ?: 0f
            return TimeInterval(timeIntervalStart, timeIntervalEnd)
        }
    }

    /**
     * Add these two intervals together, returning the smallest possible interval
     * which contains both this and other.
     */
    fun joinWith(other: ITimeInterval): ITimeInterval {
        return TimeInterval(
            maxOf(this.from, other.getStartTimeInMYA()),
            minOf(this.to, other.getEndTimeInMYA())
        )
    }
}




interface ITimeInterval {
    fun getStartTimeInMYA(): Float
    fun getEndTimeInMYA(): Float
    fun getDurationInMYA(): Float = getStartTimeInMYA() - getEndTimeInMYA()

    /**
     * Check if this interval has a non-zero overlap with the given time interval.
     * @returns true if at least one point is common to both this and the given Time Interval;
     * false otherwise.
     */
    fun overlapsWith(other: ITimeInterval): Boolean {
        val start = this.getStartTimeInMYA()
        val end = this.getEndTimeInMYA()
        val otherStart = other.getStartTimeInMYA()
        val otherEnd = other.getEndTimeInMYA()


        // e.g.
        /// larger <-                      smaller ->
        ///         [ ---- THIS ---- ... --- ]
        ///                 [ ---- OTHER ---- ]
        val overlapsOtherStart = start > otherStart && otherStart > end
        // e.g.
        ///               [ ... ----- THIS ----- ]
        ///             [ --- OTHER --- ]
        val overlapsOtherEnd = otherStart > start && start > otherEnd

        //              [ -- THIS -- ]
        //            [ ---- OTHER ---- ]
        val isSubsetOfOther = otherStart >= start && end >= otherEnd

        return overlapsOtherStart || overlapsOtherEnd || isSubsetOfOther
    }

    /**
     * Returns true if this interval contains another; this indicates that this interval is
     * strictly no smaller than the other.
     * @param other A TimeInterval to be checked against.
     * @return true if both endpoints of other are found between the endpoints of this interval;
     * false otherwise
     */
    fun contains(other: ITimeInterval): Boolean {
        return this.getStartTimeInMYA() >= other.getStartTimeInMYA()
                && other.getEndTimeInMYA() >= this.getEndTimeInMYA()
    }
}