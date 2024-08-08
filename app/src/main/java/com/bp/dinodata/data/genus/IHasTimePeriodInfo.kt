package com.bp.dinodata.data.genus

import com.bp.dinodata.data.time_period.IDisplayableTimePeriod
import com.bp.dinodata.data.time_period.ITimeInterval

interface IHasTimePeriodInfo {
    fun getTimePeriod(): IDisplayableTimePeriod?
    fun getYearsLived(): String?
    fun getTimeIntervalLived(): ITimeInterval?
    fun getStartMya(): Float?
    fun getEndMya(): Float?
}