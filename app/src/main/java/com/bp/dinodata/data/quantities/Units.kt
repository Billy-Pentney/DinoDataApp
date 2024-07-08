package com.bp.dinodata.data.quantities

sealed class Units {
    enum class Weight {
        Tonnes,                 // Metric tonnes
        TonsImperial,           // Imperial tons
        Kg,                     // Kilograms
        TonsUS
    }

    enum class Length {
        Metres,
        Centimetres,
        Feet
    }
}


object UnitConversions {
    const val CM_PER_FT = 30.48f
    const val FT_PER_METRE = 3.281f

    const val KG_PER_TON_IMP = 1016.05f
    const val KG_PER_TONNE = 1000f
    const val TONNES_PER_TON_IMP = KG_PER_TON_IMP / KG_PER_TONNE

    const val KG_PER_TON_US = 907.19f
    const val TONNES_PER_TON_US = KG_PER_TON_US / 1000f
}
