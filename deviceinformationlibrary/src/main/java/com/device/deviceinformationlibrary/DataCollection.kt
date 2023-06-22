package com.device.deviceinformationlibrary

import android.accessibilityservice.AccessibilityServiceInfo
import android.app.Activity
import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.Process
import android.os.StatFs
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

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

// total internal storage of device
fun getTotalInternalStorage(): Long {
    val path = Environment.getDataDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSizeLong
    val totalBlocks = stat.blockCountLong
    val totalInGb = totalBlocks * blockSize
    val totalInternalStorage = totalInGb / (1024 * 1024 * 1024)
    return totalInternalStorage

}


// get total external storage

fun getTotalExternalStorageSizeInGB(): Long {
    val externalStorageDirectory = Environment.getExternalStorageDirectory()
    val statFs = StatFs(externalStorageDirectory.path)

    val blockSize = statFs.blockSizeLong
    val totalBlocks = statFs.blockCountLong

    val totalBytes = blockSize * totalBlocks
    val totalGB = totalBytes / (1024 * 1024 * 1024) // Convert bytes to GB

    return totalGB
}


// this function  return the almost exact size
// Ram size
fun getTotalPhysicalMemory(): String? {
    var reader: RandomAccessFile? = null
    var load: String? = null
    val twoDecimalForm = DecimalFormat("#.##")
    var totRam = 0.0
    var lastValue = ""
    try {
        reader = RandomAccessFile("/proc/meminfo", "r")
        load = reader.readLine()

        // Get the Number value from the string
        val p: Pattern = Pattern.compile("(\\d+)")
        val m: Matcher = p.matcher(load)
        var value = ""

        while (m.find()) {
            value = m.group(1)
            // System.out.println("Ram : " + value);
        }

        reader.close()
        totRam = value.toDouble()
        // totRam = totRam / 1024;
        val mb = totRam / 1024.0
        val gb = totRam / 1048576.0
        val tb = totRam / 1073741824.0
        lastValue = if (tb > 1) {
            twoDecimalForm.format(tb) + (" TB")
        } else if (gb > 1) {
            twoDecimalForm.format(gb) + (" GB")
        } else if (mb > 1) {
            twoDecimalForm.format(mb) + (" MB")
        } else {
            twoDecimalForm.format(totRam) + (" KB")
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
    } finally {
        // Streams.close(reader);
    }
    return lastValue
}

// check Vpn is enabled or not
fun isVpnEnabled(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ?: false
    } else {
        val connectivityService =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val iConnectivityManagerClass = Class.forName(connectivityService.javaClass.name)
        val iConnectivityManagerField = iConnectivityManagerClass.getDeclaredField("mService")
        iConnectivityManagerField.isAccessible = true
        val iConnectivityManager = iConnectivityManagerField.get(connectivityService)
        val iConnectivityManagerClassStub = Class.forName(iConnectivityManager.javaClass.name)
        val getVpnConfigMethod = iConnectivityManagerClassStub.getDeclaredMethod("getVpnConfig")
        getVpnConfigMethod.isAccessible = true
        val vpnConfig = getVpnConfigMethod.invoke(iConnectivityManager)
        return vpnConfig != null
    }
}

// get screen time off
fun getScreenOffTimeout(context: Context): Long {
    return try {
        // Get the current screen timeout duration
        val timeout = Settings.System.getLong(
            context.contentResolver,
            Settings.System.SCREEN_OFF_TIMEOUT
        )
        // Convert milliseconds to seconds
        timeout / 1000
    } catch (e: Exception) {
        e.printStackTrace()
        // Handle exception if necessary
        -1L
    }
}

// check if devices rooted or not
fun isRooted(): Boolean {
    val path = System.getenv("PATH")
    val places = path.split(":")
    for (place in places) {
        val file = File(place, "su")
        if (file.exists()) {
            return true
        }
    }
    return false
}

// second Method to check that device is rooted or not
// these for some device whose not like that regular devices
//Please note that these methods are not foolproof,
//as rooted devices can use various techniques to hide their status.
//These approaches can provide a preliminary indication, but it's not guaranteed to be 100% accurate.
fun isDeviceRooted(): Boolean {
    val knownRootFileSystems =
        arrayListOf("/sbin/su", "/system/su", "/system/bin/su", "/system/xbin/su")
    for (filePath in knownRootFileSystems) {
        val file = File(filePath)
        if (file.exists()) {
            return true
        }
    }
    return false
}

// number of cores of the device cpu
fun getNumberOfCores(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Runtime.getRuntime().availableProcessors()
    } else {
        // For older devices, use a different method to get the number of cores
        try {
            val file = File("/sys/devices/system/cpu/")
            val files = file.listFiles { _, name -> name.startsWith("cpu") }
            files.size
        } catch (e: Exception) {
            Runtime.getRuntime().availableProcessors()
            Log.d("exceptionMessage", e.message.toString())
        }
    }
}

//indicate device is possible emulator or not

fun isEmulator(): Boolean {
    return (Build.FINGERPRINT.contains("generic")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || "google_sdk" == Build.PRODUCT)
}


// get device timezone offset
fun getDeviceTimeZoneOffset(): Int {
    val currentTimeZone = TimeZone.getDefault()
    val offsetInMillis = currentTimeZone.getOffset(Calendar.getInstance().timeInMillis)
    // convert offsetInMillis to hours
    return offsetInMillis / (1000 * 60 * 60)
}

// Function to check if Auto Net selection is enabled
fun isAutoNetSelectionEnabled(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Check if the device is running on Android Q or higher
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == true
    }

    // Auto Net selection not applicable for Android versions below Q
    return false
}

// check permission function
// Check if the app has the necessary permissions to access the Wi-Fi state
private fun hasPermission(context: Activity, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

// Get the MAC address of the Wi-Fi hardware
fun getMacAddress(context: Context): String? {
    val wifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // Check if the app has the necessary permissions
    if (hasPermission(context as Activity, android.Manifest.permission.ACCESS_WIFI_STATE)) {
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo
        if (wifiInfo != null) {
            return wifiInfo.macAddress
        }
    } else {
        // Request the necessary permissions if not granted
        ActivityCompat.requestPermissions(
            context,
            arrayOf(android.Manifest.permission.ACCESS_WIFI_STATE),
            1
        )
    }

    return null
}

// check closed captioning enabled or not
fun isClosedCaptioningEnabled(context: Context): Boolean {
    val captioningManager =
        context.getSystemService(Context.CAPTIONING_SERVICE) as android.view.accessibility.CaptioningManager

    return captioningManager.isEnabled
}


// check talk back option enabled or not
fun Context.isScreenReaderOn(): Boolean {
    val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    if (am != null && am.isEnabled) {
        val serviceInfoList =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
        if (!serviceInfoList.isEmpty())
            return true
    }
    return false
}
