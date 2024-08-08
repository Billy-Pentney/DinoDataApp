package com.bp.dinodata.data.filters

import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.genus.IHasDiet

class DietFilter(
    private val acceptedDiets: List<Diet>
): IFilter<IHasDiet> {
    override fun acceptsItem(item: IHasDiet): Boolean {
        return item.getDiet() in acceptedDiets
    }
    override fun toString(): String {
        return "DIET in [${acceptedDiets.joinToString(",")}]"
    }
}