package com.example.mywardrobe.core

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.mywardrobe.R
import com.example.mywardrobe.adapter.ViewPagerAdapter
import com.example.mywardrobe.utils.GeneralUtils
import com.example.mywardrobe.utils.WardrobeTypeEnum
import com.example.mywardrobe.viewmodel.WardrobeViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var capturedImageFilePath: String? = null
    private lateinit var wardrobeEnum: WardrobeTypeEnum
    private var wardrobeViewModel: WardrobeViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUi()
        observeStates()
    }

    private fun initUi() {
        wardrobeViewModel = ViewModelProvider(this).get(WardrobeViewModel::class.java)

        btnAddShirt.setOnClickListener(this)
        btnAddTrousers.setOnClickListener(this)
        imgShuffle.setOnClickListener(this)
        imgFav.setOnClickListener(this)
        imgFav.isSelected = false

        vpShirt.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                checkFavOnSwipeShirtVp(position)
                super.onPageSelected(position)
            }
        })

        vpTrousers.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                checkFavOnSwipeTrouserVp(position)
                super.onPageSelected(position)
            }
        })
    }

    private fun observeStates() {

        val shirtAdapter = ViewPagerAdapter(ArrayList())
        val trousersAdapter = ViewPagerAdapter(ArrayList())
        vpShirt.adapter = shirtAdapter
        vpTrousers.adapter = trousersAdapter

        wardrobeViewModel?.apply {
            shirtList?.observe(this@MainActivity, {
                it?.let {
                    if (it.isNotEmpty()) {
                        shirtAdapter.addItemList(ArrayList(it))
                    }
                    initFavAndShuffle()
                }
            })

            trousersList?.observe(this@MainActivity, {
                it?.let {
                    if (it.isNotEmpty()) {
                        trousersAdapter.addItemList(ArrayList(it))
                    }
                    initFavAndShuffle()
                }
            })
        }
    }

    private fun initFavAndShuffle() {
        imageFavShuffle.visibility = if (wardrobeViewModel?.shirtList?.value?.isNotEmpty() == true
            && wardrobeViewModel?.trousersList?.value?.isNotEmpty() == true
        ) {
            checkFavOnSwipeShirtVp(vpShirt.currentItem)
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun checkFavOnSwipeShirtVp(position: Int) {
        wardrobeViewModel?.shirtList?.value?.get(position)?.favCombinationItemList.let { trouserList ->
            if (trouserList?.isNotEmpty() == true && wardrobeViewModel?.trousersList?.value?.isNotEmpty() == true) {
                val trouserID =
                    wardrobeViewModel?.trousersList?.value?.get(vpTrousers.currentItem)?.primaryId
                imgFav.isSelected = trouserList.contains(trouserID)
            } else {
                imgFav.isSelected = false
            }
        }
    }

    fun checkFavOnSwipeTrouserVp(position: Int) {
        wardrobeViewModel?.trousersList?.value?.get(position)?.favCombinationItemList.let { shirtList ->
            if (shirtList?.isNotEmpty() == true && wardrobeViewModel?.shirtList?.value?.isNotEmpty() == true) {
                val shirtID =
                    wardrobeViewModel?.shirtList?.value?.get(vpShirt.currentItem)?.primaryId
                imgFav.isSelected = shirtList.contains(shirtID)
            } else {
                imgFav.isSelected = false
            }
        }
    }

    private fun addWardrobe(wardrobe: WardrobeTypeEnum) {
        wardrobeEnum = wardrobe
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST
            )
        } else {
            openAlertDialog()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST && GeneralUtils.hasAllPermissionsGranted(grantResults)) {
            openAlertDialog()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun openAlertDialog() {
        LayoutInflater.from(this).inflate(R.layout.layout_custom_alert_dialog, null)?.apply {
            val alertDialog =
                AlertDialog.Builder(this@MainActivity, R.style.MyCustomTheme).setView(this).show()

            findViewById<AppCompatTextView>(R.id.tvGallery)?.setOnClickListener {
                openGallery()
                alertDialog?.dismiss()
            }

            findViewById<AppCompatTextView>(R.id.tvCamera)?.setOnClickListener {
                openCamera()
                alertDialog?.dismiss()
            }

            findViewById<AppCompatTextView>(R.id.tvCancel)?.setOnClickListener {
                alertDialog?.dismiss()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GET_SINGLE_FILE)
    }

    private fun openCamera() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pictureIntent.resolveActivity(packageManager)?.also {
            var photoFile: File?
            try {
                photoFile = GeneralUtils.createImageFile(this)
            } catch (ioe: IOException) {
                photoFile = null
                ioe.printStackTrace()
            }
            photoFile?.let {
                startCameraForResult(it, pictureIntent)
            } ?: run {
                photoFile = GeneralUtils.createImageFile(this)
                photoFile?.let {
                    startCameraForResult(it, pictureIntent)
                }
            }
        } ?: run {
            Toast.makeText(this, getString(R.string.error_msg), Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCameraForResult(photoFile: File, pictureIntent: Intent) {
        capturedImageFilePath = photoFile.absolutePath
        val photoURI = FileProvider.getUriForFile(
            this,
            "$packageName.provider", photoFile
        )
        pictureIntent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            photoURI
        )
        startActivityForResult(
            pictureIntent,
            REQUEST_IMAGE_CAPTURE
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            wardrobeViewModel?.insertWardrobeToDb(wardrobeEnum, capturedImageFilePath)
        }

        if (requestCode == REQUEST_GET_SINGLE_FILE && resultCode == RESULT_OK) {
            data?.data?.let { addImageLocation(it) }
        }
    }

    private fun addImageLocation(data: Uri) {
        val selectedImage: Uri = data
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(
            selectedImage,
            filePathColumn, null, null, null
        )
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val picturePath = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        wardrobeViewModel?.insertWardrobeToDb(wardrobeEnum, picturePath)
    }


    companion object {
        const val PERMISSION_REQUEST = 198
        const val REQUEST_IMAGE_CAPTURE = 99
        const val REQUEST_GET_SINGLE_FILE = 77
    }

    override fun onClick(views: View?) {
        when (views?.id) {
            R.id.btnAddShirt -> {
                addWardrobe(WardrobeTypeEnum.SHIRT)
            }

            R.id.btnAddTrousers -> {
                addWardrobe(WardrobeTypeEnum.TROUSERS)
            }

            R.id.imgShuffle -> {
                AlertDialog.Builder(this).setMessage(getString(R.string.do_you_want_to_shuffle))
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        dialog?.dismiss()
                        vpShirt.currentItem =
                            (0..(wardrobeViewModel?.shirtList?.value?.size ?: 0)).random()
                        vpTrousers.currentItem =
                            (0..(wardrobeViewModel?.trousersList?.value?.size ?: 0)).random()
                    }
                    .setNegativeButton(getString(R.string.no)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }

            R.id.imgFav -> {
                imgFav.isSelected = !imgFav.isSelected
                wardrobeViewModel?.apply {
                    updateFav(
                        shirtList?.value?.get(vpShirt.currentItem)?.primaryId ?: 0,
                        trousersList?.value?.get(vpTrousers.currentItem)?.primaryId ?: 0,
                        imgFav.isSelected
                    )
                }
            }
        }
    }
}