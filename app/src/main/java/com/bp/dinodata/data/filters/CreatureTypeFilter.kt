package com.bp.dinodata.data.filters

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.genus.IHasCreatureType

class CreatureTypeFilter(
    private val requiredTypes: List<CreatureType>
): IFilter<IHasCreatureType> {
    override fun acceptsItem(item: IHasCreatureType): Boolean = item.getCreatureType() in requiredTypes
    override fun toString(): String {
        return "TYPE in [${requiredTypes.joinToString(",")}]"
    }
}