package com.example.mywardrobe.core

import android.app.Application
import com.example.mywardrobe.db.WardrobeDataBase

class App : Application(){

    override fun onCreate() {
        super.onCreate()
        WardrobeDataBase.getInstance(applicationContext)
    }
}