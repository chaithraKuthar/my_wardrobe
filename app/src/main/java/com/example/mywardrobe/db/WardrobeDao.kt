package com.example.mywardrobe.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mywardrobe.utils.WardrobeTypeEnum
import com.example.mywardrobe.db.entity.Wardrobe

@Dao
interface WardrobeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)  // or OnConflictStrategy.IGNORE
    fun insertWardrobeItem(wardrobe: Wardrobe): Long

    @Query("SELECT * from ${Wardrobe.TABLE_NAME} where itemType =:wardrobeItemType")
    fun getAllShirt(wardrobeItemType: Int = WardrobeTypeEnum.SHIRT.typeID): LiveData<List<Wardrobe>>

    @Query("SELECT * from ${Wardrobe.TABLE_NAME} where itemType =:wardrobeItemType")
    fun getAllTrousers(wardrobeItemType: Int = WardrobeTypeEnum.TROUSERS.typeID): LiveData<List<Wardrobe>>

    @Query("UPDATE ${Wardrobe.TABLE_NAME} SET favCombinationItemList =:favItemList where primaryId = :itemId")
    fun updateFavList(itemId: Int, favItemList: ArrayList<Int>?)

    @Query("SELECT * from ${Wardrobe.TABLE_NAME} where primaryId = :itemId")
    fun getFavList(itemId: Int): Wardrobe
}