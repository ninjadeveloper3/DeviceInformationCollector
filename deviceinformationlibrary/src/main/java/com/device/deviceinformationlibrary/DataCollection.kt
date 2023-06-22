package com.device.deviceinformationlibrary

import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.Inet4Address
import java.net.NetworkInterface

object DataCollection {

    // get Timestamp
    fun getTimeStamp(): String {
        // current time in milis
        val currentTimeMillis = System.currentTimeMillis()
        //timestamp
        val timeStamp = java.sql.Timestamp(currentTimeMillis)

        return timeStamp.toString()
    }

    // get device name
    fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model
        } else {
            "$manufacturer $model"
        }
    }


    //get device CPU clock speed
    fun getDeviceCpuClockSpeed(): String {
        try {
            val pid = Process.myPid().toString()
            val cpuInfoFile = File("/proc/$pid/stat")
            val reader = BufferedReader(FileReader(cpuInfoFile))
            val cpuInfo = reader.readLine().split(" ").toTypedArray()
            reader.close()

            // CPU clock speed is the 14th value in the cpuInfo array
            val clockSpeed = cpuInfo[13]

            Log.d("clockSpeed", clockSpeed.toString())
            // Convert from Hz to MHz
            //  val clockSpeedMHz = clockSpeed.toLong() / 1000

            return "$clockSpeed"
            //  return "clockSpeedMHz MHz"
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return "N/A"
    }

    // android OSVersion
    fun getDeviceAndroidOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    // get build version
    fun getDeviceBuildVersion(): Int {
        return Build.VERSION.SDK_INT
    }


// get the company name who manufacture the device

    fun getDeviceManufacturerName(): String {
        return Build.MANUFACTURER
    }

    // ipv4 ip address
    fun getAddress(): String {
        NetworkInterface.getNetworkInterfaces()?.toList()?.map { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find {
                !it.isLoopbackAddress && it is Inet4Address
            }?.let { return it.hostAddress }
        }
        return ""
    }

    // android device Id
    fun getAndroidDeviceId(context: Context): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }


    // get available internal storage in GB
    fun getAvailableInternalStorageInGB(): Long {

        // Get the file system for the internal storage
        val statFs = StatFs(Environment.getDataDirectory().path)

        // Get the available blocks on the file system
        val availableBlocks = statFs.availableBlocksLong

        // Get the block size on the file system
        val blockSize = statFs.blockSizeLong

        // Calculate the available space in bytes
        val availableSpaceInBytes = availableBlocks * blockSize

        // Convert the available space to GB
        val availableSpaceInGB = availableSpaceInBytes / (1024 * 1024 * 1024)

        return availableSpaceInGB
    }

    // available external storage
    fun getAvailableExternalStorage(): Long {
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        val stat = StatFs(externalStorageDirectory.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        val availableBytes = availableBlocks * blockSize
        val availableGB = availableBytes / (1024 * 1024 * 1024) // Convert to GB
        return availableGB
    }

    // Function to get the file path of the currently used wallpaper
    fun getCurrentWallpaperPath(context: Context): String? {
        val wallpaperManager = WallpaperManager.getInstance(context)
        val wallpaperDrawable = wallpaperManager.drawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
            val hasPermission = ContextCompat.checkSelfPermission(context, permission)

            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                // requestPermissions(arrayOf(permission), 101)
            } else {
                if (wallpaperDrawable != null) {
                    val contentResolver: ContentResolver = context.contentResolver
                    val wallpaperUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                    // Projection to specify the columns to retrieve from the MediaStore
                    val projection = arrayOf(MediaStore.Images.Media.DATA)

                    // Sort order to retrieve the latest image
                    val sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC"

                    // Query the MediaStore to get the file path of the latest image
                    contentResolver.query(wallpaperUri, projection, null, null, sortOrder)
                        ?.use { cursor ->
                            if (cursor.moveToFirst()) {
                                val columnIndex =
                                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                                return cursor.getString(columnIndex)
                            }
                        }
                }
                // Permission is already granted, proceed with accessing the wallpaper drawable
                // ...
            }
        } else {
            // For devices below Marshmallow, permission is granted by default
            // Proceed with accessing the wallpaper drawable
            //
        }
        return null
    }

}

// detect dark mode or light mode
fun isDarkMode(context: Context): Boolean {
    val currentNightMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return currentNightMode == Configuration.UI_MODE_NIGHT_YES
}