package com.bp.dinodata.data

import com.bp.dinodata.data.attributions.ResourceAttribution
import com.bp.dinodata.presentation.detail_creature_type.IDescribesAttribution
import com.bp.dinodata.presentation.detail_creature_type.TextWithAttribution

class CreatureTypeInfo(
    private val type: CreatureType,
    private val description: String,
    private val descriptionAttribution: ResourceAttribution? = null
): ICreatureTypeInfo {
    constructor(type: CreatureType, description: String): this(
        type, description, null
    )

    override fun getCreatureType(): CreatureType = type
    override fun getDescriptionText(): String = description
    override fun getTextAttributionOrNull(): String? {
        return descriptionAttribution?.let {
            "Text provided by ${it.source}"
        }
    }
}

interface ICreatureTypeInfo: IDescribesAttribution {
    fun getDescriptionText(): String
    fun getCreatureType(): CreatureType
}