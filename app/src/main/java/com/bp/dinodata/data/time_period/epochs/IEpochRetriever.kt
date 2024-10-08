package com.bp.dinodata.data.time_period.epochs


interface IEpochRetriever<T: IEpochId> {
    fun getEpoch(id: T): Epoch
}

interface IEpochRetrieverGeneral {
    @Throws(UnrecognisedEpochId::class)
    fun getEpoch(id: IEpochId): IEpoch?
}

object EpochRetriever: IEpochRetrieverGeneral {
    @Throws(UnrecognisedEpochId::class)
    override fun getEpoch(id: IEpochId): IEpoch {
        return when (id) {
            is MesozoicEpochs.MesozoicEpochId -> MesozoicEpochs.getEpoch(id)
            is CenozoicEpochs.CenozoicEpochId -> CenozoicEpochs.getEpoch(id)
            is PaleozoicEpochs.PaleozoicEpochId -> PaleozoicEpochs.getEpoch(id)
            else -> throw UnrecognisedEpochId(id)
        }
    }
}