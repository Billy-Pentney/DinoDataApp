package com.bp.dinodata.data.quantities

sealed class Units {
    enum class Weight {
        Tonnes,         // Metric tonnes
        Tons,           // Imperial tons
        Kg              // Kilograms
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

    const val KG_PER_TON = 1016
    const val KG_PER_TONNE = 1000
    const val TONNES_PER_TON = 1.016f
}
