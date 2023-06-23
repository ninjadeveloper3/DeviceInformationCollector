package com.device.deviceinformationlibrary

import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.*
import java.net.Inet4Address
import java.net.NetworkInterface
import java.text.DecimalFormat
import java.text.SimpleDateFormat
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
    fun getIpAddress(): String {
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
//    fun Context.isScreenReaderOn(): Boolean {
//        val am = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
//        if (am != null && am.isEnabled) {
//            val serviceInfoList =
//                am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
//            if (!serviceInfoList.isEmpty())
//                return true
//        }
//        return false
//    }

    // get last boot time of device
    fun getBootTime(format: String): String {

        // Get the system boot time.
        val bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime()

        // Format the boot time.
        val formattedBootTime = SimpleDateFormat(format).format(Date(bootTime))

        // Return the formatted boot time.
        return formattedBootTime
    }


// check color invision enabled
    /** Color Inversion */
    fun isColorInversionEnabled(context: Context): Boolean? {
        return try {
            Settings.Secure.getInt(
                context.contentResolver,
                "accessibility_display_inversion_enabled",
                0
            ) == 1
        } catch (e: Exception) {
            null
        }

    }


    /** Alternative Id for IDFV (ios) */
    fun generateUUID(): String {
        val uuid: UUID = UUID.randomUUID()
        return uuid.toString()
    }

    /** Device Language */
    fun getDeviceLanguage(context: Context): String {

        val locale: Locale = context.resources.configuration.locales[0]

        return locale.language
    }


    /** Wifi SSID */
    fun getWifiSSID(context: Context): String? {

        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo: WifiInfo? = wifiManager.connectionInfo

        return wifiInfo?.ssid?.removeSurrounding("\"")
    }

    /** Device Width and Height*/
    fun getDeviceWidthAndHeight(context: Context): Pair<Int, Int> {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        val widthPixels = displayMetrics.widthPixels
        val heightPixels = displayMetrics.heightPixels

        return Pair(widthPixels, heightPixels)
    }


    /** Keyboards List */
    fun getInstalledKeyboards(context: Context): List<String> {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val inputMethodList = inputMethodManager.enabledInputMethodList

        val installedKeyboards = mutableListOf<String>()
        for (inputMethodInfo in inputMethodList) {
            val packageName = inputMethodInfo.packageName
            installedKeyboards.add(packageName)
        }
        return installedKeyboards
    }


    /** Kernel Version */
    fun getKernelVersion(command: String): String {
        var kernelVersion = ""
        try {
            val process = Runtime.getRuntime().exec(command)
            val inputStream = process.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            kernelVersion = bufferedReader.readLine()
            bufferedReader.close()
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return kernelVersion
    }

    // calendar type of device
    @RequiresApi(Build.VERSION_CODES.O)
    fun getDeviceCalendarType(): String {
        val calendar = Calendar.getInstance()
        return calendar.getCalendarType()
    }

    fun deviceFontList(): Array<File> {
        // return font list
        val path = "/system/fonts"
        val file = File(path)
        val ff: Array<File> = file.listFiles()
        return ff
    }

    // get location of device
    @SuppressLint("MissingPermission", "SetTextI18n")
     fun getLocation(context: Activity , mFusedLocationClient :FusedLocationProviderClient) :String {
                var latitude:String?= null
                var longitude:String?= null
                var accuracy:String?= null

                mFusedLocationClient.lastLocation.addOnCompleteListener(context) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(context, Locale.getDefault())

                         latitude =    location.latitude.toString()
                         longitude=  location.longitude.toString()
                         accuracy  = location.accuracy.toString()


                    }
            }

          return "latitude: $latitude,  longitude: $longitude, accuracy: $accuracy"
    }

    // Kernel Arch
    fun kernelArchitecture(): String {
        val kernelArchitecture = System.getProperty("os.arch")
        return kernelArchitecture
    }

    // device orientation
    fun deviceOrientation(context: Context): Int {
        // get screen orientation second method
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.getDefaultDisplay()

        val rotation = display.getRotation()
        return rotation
    }
    // auto timezone enabled or not
    fun autoTimeZoneEnabled(context: Context):String{
        val autoTimeZoneSettings =
            Settings.Global.getString(context.contentResolver, Settings.Global.AUTO_TIME_ZONE)
        return autoTimeZoneSettings
    }
    // Function to check if the device is connected to a Wi-Fi network
    fun isWifiConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
    }
    // check talk back option enabled or not
    fun isScreenReaderOn(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        if (am != null && am.isEnabled) {
            val serviceInfoList =
                am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_SPOKEN)
            if (!serviceInfoList.isEmpty())
                return true
        }
        return false
    }

    // device Model
    fun deviceModel() : String{
        val deviceModel = Build.MODEL
        return deviceModel
    }
    // for getting career name
     fun getAndDisplayCarrierName(context: Context): String {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.networkOperatorName
    }

    // get country name
    fun countryName(context: Context): String {
        val country = context.resources.configuration.locale.country
        return country
    }

    // cpu Type
    fun cpuType(): String {
        val cpuType = Build.CPU_ABI
        return cpuType
    }
    // check if Apppinned
     fun isAppPinned(context: Context) : Boolean{
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val pinned = activityManager.lockTaskModeState
        val isPinned = pinned != 0
        return isPinned
    }


}

