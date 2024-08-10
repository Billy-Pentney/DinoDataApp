package com.bp.dinodata.data.time_period.epochs

interface IEpochManager<T: IEpochId>: IEpochRetriever<T> {
    fun getAll(): List<IEpoch>
    fun getEnumFromString(text: String): IEpochId?
}