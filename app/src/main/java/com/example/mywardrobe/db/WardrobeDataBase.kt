package com.example.mywardrobe.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mywardrobe.db.entity.Wardrobe

@Database(entities = [Wardrobe::class], version = 1, exportSchema = false)
@TypeConverters(WardrobeTypeConverter::class)
abstract class WardrobeDataBase : RoomDatabase() {

    abstract fun getWardrobeDao(): WardrobeDao

    companion object {

        private var INSTANCE: WardrobeDataBase? = null

        fun getInstance(context: Context): WardrobeDataBase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    WardrobeDataBase::class.java,
                    "wardrobe_db.db"
                ).allowMainThreadQueries()
                    .build()
            }
            return INSTANCE
        }
    }
}