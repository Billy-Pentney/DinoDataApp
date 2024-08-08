package com.bp.dinodata.data.enum_readers

import android.util.Log
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.CreatureTypes

object CreatureTypeConverter: TypeConverter<CreatureType>(
    dataMap = CreatureTypes.CreatureTypeMap,
    logTag = "CreatureTypeConverter"
) {

    override fun preprocessText(text: String): String {
        return text.lowercase().replace(" ","_")
    }
}