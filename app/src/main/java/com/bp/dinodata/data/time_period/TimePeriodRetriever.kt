package com.bp.dinodata.data.time_period

interface IRetrievesEra {
    @Throws(UnrecognisedEraId::class)
    fun getEra(id: IEraId): ITimeEra
}

object EraRetriever: IRetrievesEra {
    override fun getEra(id: IEraId): ITimeEra {
        if (id !is EraId) {
            throw UnrecognisedEraId(id)
        }

        return when (id) {
            EraId.Mesozoic -> Eras.Mesozoic
            EraId.Paleozoic -> Eras.Paleozoic
            EraId.Cenozoic -> Eras.Cenozoic
        }
    }
}

