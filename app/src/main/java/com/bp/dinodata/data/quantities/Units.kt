package com.bp.dinodata.data.quantities


interface IUnit

enum class LengthUnit: IUnit {
    METRE,
    CENTIMETRE,
    FOOT
}

enum class MassUnit: IUnit {
    TONNE,                         // Metric tonnes
    TON_IMPERIAL,                   // Imperial tons
    KILOGRAM,                      // Kilograms
    TON_US
}

object UnitFormatter {
    private fun appendPluralS(str: String, isPlural: Boolean): String {
        return if (isPlural) "${str}s" else str
    }

    fun convertToString(unit: MassUnit, isPlural: Boolean): String {
        return when (unit) {
            MassUnit.TONNE -> appendPluralS("tonne", isPlural)
            MassUnit.TON_IMPERIAL -> appendPluralS("imperial ton", isPlural)
            MassUnit.KILOGRAM -> "kg"       // no plural here
            MassUnit.TON_US -> appendPluralS("US ton", isPlural)
        }
    }

    fun convertToString(unit: LengthUnit, isPlural: Boolean): String {
        return when (unit) {
            LengthUnit.METRE -> appendPluralS("metre", isPlural)
            LengthUnit.CENTIMETRE -> appendPluralS("centimetre", isPlural)
            LengthUnit.FOOT -> {
                if (isPlural) {
                    "feet"
                }
                else {
                    "foot"
                }
            }
        }
    }
}

object UnitParser {

    private val MASS_UNIT_KEY = mapOf(
        "kg" to MassUnit.KILOGRAM,
        "us_ton" to MassUnit.TON_US,
        "imp_ton" to MassUnit.TON_IMPERIAL,
        "tonne" to MassUnit.TONNE
    )

    private val LEN_UNIT_MAP = mapOf(
        "metre" to LengthUnit.METRE,
        "centimetre" to LengthUnit.CENTIMETRE,
        "foot" to LengthUnit.FOOT,
    )

    fun parseMassUnit(rawStr: String): MassUnit? {
        return MASS_UNIT_KEY[rawStr]
    }

    fun parseLengthUnit(rawStr: String): LengthUnit? {
        return LEN_UNIT_MAP[rawStr]
    }
}