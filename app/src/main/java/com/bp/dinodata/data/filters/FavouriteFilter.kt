package com.bp.dinodata.data.filters

import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.genus.ILocalPrefs

class FavouriteFilter(
    private val acceptFavourites: Boolean? = null
): IFilter<IGenus> {
    override fun acceptsItem(item: IGenus): Boolean {
        if (acceptFavourites == null) {
            // Accept all items if an invalid argument is given
            return true
        }
        // Otherwise, accept mutually-exclusively, i.e.
        // if the argument is true, accept only favourites; if false, then accept only non-favourites
        if (item is ILocalPrefs) {
            return (acceptFavourites && item.isUserFavourite()) ||
                    (!acceptFavourites && !item.isUserFavourite())
        }
        return false
    }
}