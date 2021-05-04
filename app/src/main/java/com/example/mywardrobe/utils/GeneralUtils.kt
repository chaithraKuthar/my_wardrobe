package com.example.mywardrobe.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import java.io.File
import java.io.IOException
import java.util.*

object GeneralUtils {

    fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    @Throws(IOException::class)
    fun createImageFile(context: Context?): File {
        val timeStamp = Calendar.getInstance().timeInMillis.toString()
        val imageFileName = "WARDROBE_$timeStamp.jpg"
        return File(
            context?.getExternalFilesDir(
                Environment.DIRECTORY_DCIM
            ), imageFileName
        )
    }
}