package com.example.mywardrobe.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mywardrobe.db.entity.Wardrobe.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
data class Wardrobe(
    var itemType: Int,
    var itemPath: String?,
    var favCombinationItemList: ArrayList<Int>? = null,
    @PrimaryKey(autoGenerate = true)
    var primaryId: Int = 0
) {
    companion object {
        const val TABLE_NAME = "Wardrobe"
    }
}