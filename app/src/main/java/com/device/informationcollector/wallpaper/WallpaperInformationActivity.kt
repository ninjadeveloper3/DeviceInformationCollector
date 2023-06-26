package com.device.informationcollector.wallpaper

import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.device.deviceinformationlibrary.DataCollection
import com.device.deviceinformationlibrary.DataCollection.getCurrentWallpaperPath
import com.device.informationcollector.databinding.ActivityWallpaperInformationBinding
import com.device.informationcollector.dialog.DialogUtils
import com.device.informationcollector.permission.PermissionCheckers

class WallpaperInformationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWallpaperInformationBinding
    private lateinit var permissionCheckers: PermissionCheckers
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWallpaperInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initContactPermission()
    }


    private fun initViews() {
        progressDialog = ProgressDialog(this)
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
        permissionCheckers = PermissionCheckers(this)
    }

    private fun initContactPermission() {
        if (permissionCheckers.isPermissionGranted(PermissionCheckers.EXTERNAL_STORAGE)) {
            getWallpaperInfo()
        } else {
            permissionCheckers.requestPermission(
                PermissionCheckers.EXTERNAL_STORAGE,
                PermissionCheckers.EXTERNAL_STORAGE_CODE
            )
        }
    }


    private fun getWallpaperInfo() {
        // get wallpaper name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            wallpaperInfo()
        } else {
            wallpaperInfo()
            // For devices below Marshmallow, permission is granted by default
            // Proceed with accessing the wallpaper drawable
        }
    }


    // get the current wallpaper info
    private fun wallpaperInfo() {

        // here is permission check is neccessary because
        // because without check the wallpaper maanager give errors
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        val hasPermission = ContextCompat.checkSelfPermission(this, permission)

        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            requestPermissions(arrayOf(permission), 101)
        } else {
            // get wallpaper name
            val wallpaperManager = WallpaperManager.getInstance(this)
            val wallpaperDrawable = wallpaperManager.drawable
            if (wallpaperDrawable is BitmapDrawable) {
                val bitmap = wallpaperDrawable.bitmap
                binding.imageView.setImageBitmap(bitmap)

                // get path of image and then get name from path
                // get path of wallpaper
                // using this function we can get the path of wallpaper
                // and using path we can get the name of wallpaper
                val wallpaperPath = DataCollection.getCurrentWallpaperPath(applicationContext)
                if (wallpaperPath != null) {
                    // Do something with the wallpaper file path
                    val imageName = wallpaperPath.substringAfterLast("/")
                    binding.wallpaperNameTv.text = imageName
                } else {
                    // Wallpaper path not found
                    println("Unable to retrieve current wallpaper path")
                }
            } else {
                // The drawable is not a BitmapDrawable
                // Handle the case accordingly
                // ...
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionCheckers.EXTERNAL_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PermissionCheckers.GRANTED) {
                getWallpaperInfo()
            } else {
                if (!shouldShowRequestPermissionRationale(PermissionCheckers.EXTERNAL_STORAGE)) {
                    DialogUtils.goToSystemLocationSetting(
                        this, PermissionCheckers.EXTERNAL_STORAGE_CUSTOM_DIALOG_MESSAGE
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getWallpaperInfo()
    }

}