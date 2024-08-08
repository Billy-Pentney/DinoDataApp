package com.bp.dinodata.data.time_period.epochs

import com.bp.dinodata.data.time_period.IEpoch
import com.bp.dinodata.data.time_period.IEpochId

interface IEpochManager<T: IEpochId>: IEpochRetriever<T> {
    fun getAll(): List<IEpoch>
    fun getEnumFromString(text: String): IEpochId?
}