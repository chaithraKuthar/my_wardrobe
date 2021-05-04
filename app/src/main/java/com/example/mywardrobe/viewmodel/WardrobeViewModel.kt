package com.example.mywardrobe.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mywardrobe.db.WardrobeDataBase
import com.example.mywardrobe.db.entity.Wardrobe
import com.example.mywardrobe.utils.WardrobeTypeEnum

class WardrobeViewModel(application: Application) : AndroidViewModel(application) {

    var shirtList = WardrobeDataBase.getInstance(application)?.getWardrobeDao()?.getAllShirt()
    var trousersList = WardrobeDataBase.getInstance(application)?.getWardrobeDao()?.getAllTrousers()

     fun insertWardrobeToDb(wardrobe: WardrobeTypeEnum, path: String?) {
        WardrobeDataBase.getInstance(getApplication())?.getWardrobeDao()
            ?.insertWardrobeItem(Wardrobe(itemPath = path, itemType = wardrobe.typeID))
    }

    fun updateFav(shirtId: Int, trouserId: Int, selected: Boolean) {
        WardrobeDataBase.getInstance(getApplication())?.getWardrobeDao()?.apply {

            val favTrouserList = getFavList(shirtId).favCombinationItemList?.let {
                ArrayList(it)
            } ?: ArrayList()
            val favShirtList = getFavList(trouserId).favCombinationItemList?.let {
                ArrayList(it)
            } ?: ArrayList()

            if (selected) {
                favTrouserList.add(trouserId)
                favShirtList.add(shirtId)
            } else {

                if (favTrouserList.contains(trouserId)) {
                    favTrouserList.remove(trouserId)
                }

                if (favShirtList.contains(shirtId)) {
                    favShirtList.remove(shirtId)
                }
            }

            updateFavList(shirtId, favTrouserList)
            updateFavList(trouserId, favShirtList)
        }


    }

}