package com.device.informationcollector

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.device.deviceinformationlibrary.DataCollection
import com.device.informationcollector.databinding.ActivityMainBinding
import com.device.informationcollector.dialog.DialogUtils
import com.device.informationcollector.font.FontListActivity
import com.device.informationcollector.permission.PermissionCheckers

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionCheckers: PermissionCheckers

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setViews()
        initClick()

    }

    private fun initViews() {

        permissionCheckers = PermissionCheckers(this)
        initFineLocationPermission()
        initPhoneStatePermission()

    }

    private fun initClick() {
        binding.listOfAvailableFonts.setOnClickListener {
            val intent = Intent(this, FontListActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setViews() {
        // set current timestamp
        binding.timeStampTv.text = DataCollection.getTimeStamp()
        // set device name
        binding.deviceNameTv.text = DataCollection.getDeviceName()
        // set manufacturer name
        binding.manufacturerNameTv.text = DataCollection.getDeviceManufacturerName()
        // set OS version
        binding.versionOsTv.text = DataCollection.getDeviceAndroidOSVersion()
        // set build version
        binding.buildVersionTv.text = DataCollection.getDeviceBuildVersion().toString()
        // set device ip over network
        binding.deviceIpTv.text = DataCollection.getIpAddress()
        // set android device id
        binding.androidDeviceIDTv.text = DataCollection.getAndroidDeviceId(this)
        // set device cpu clock speed
        binding.cpuClockSPeedTv.text = DataCollection.getDeviceCpuClockSpeed()
        // set available internal storage
        binding.availableInternalStorageTv.text =
            DataCollection.getAvailableInternalStorageInGB().toString()
        // set available external storage
        binding.availableExternalStorageTv.text =
            DataCollection.getAvailableExternalStorage().toString()
        //check device theme mode
        val isDarkMode = DataCollection.isDarkMode(this)
        if (isDarkMode) {
            binding.deviceThemeModeTv.text = getString(R.string.dark_mode)
        } else {
            binding.deviceThemeModeTv.text = getString(R.string.light_mode)
        }
        //set total internal storage
        val totalInternal = DataCollection.getTotalInternalStorage().toString()
        binding.totalInternalStorageTv.text = "$totalInternal GB"
        // set total external storage
        val totalExternal = DataCollection.getTotalExternalStorageSizeInGB().toString()
        binding.totalExternalStorageTv.text = "$totalExternal GB"

        // set physical memory RAM
        binding.totalPhysicalMemoryTv.text = DataCollection.getTotalPhysicalMemory()
        // check vpn status
        val isVpnEnabled = DataCollection.isVpnEnabled(this)
        if (isVpnEnabled) {
            binding.isVpnEnabledTv.text = getString(R.string.enabled)
        } else {
            binding.isVpnEnabledTv.text = getString(R.string.disabled)
        }

        // set screen offtime in seconds
        binding.screenOffTimeTv.text = DataCollection.getScreenOffTimeout(this).toString()
        // check device isRooted
        val deviceRooted = DataCollection.isDeviceRooted()
        if (deviceRooted) {
            binding.deviceRootedTv.text = getString(R.string.device_is_rooted)
        } else {
            binding.deviceRootedTv.text = getString(R.string.device_not_roote)

        }

        // check number of cores cpu
        binding.cpuCoresTv.text = DataCollection.getNumberOfCores().toString()
        // check device is emulator
        val isDeviceEmulator = DataCollection.isEmulator()
        if (isDeviceEmulator) {
            binding.deviceEmulatorTv.text = getString(R.string.device_is_emulator)
        } else {
            binding.deviceEmulatorTv.text = getString(R.string.device_not_emulator)
        }
        // set kernal arch
        binding.kernelArchTv.text = DataCollection.kernelArchitecture()
        // set device timezoenoffset
        val deviceTimeZone = DataCollection.getDeviceTimeZoneOffset().toString()
        binding.deviceTimeZoneTv.text = "$deviceTimeZone hours"
        // check device screen orientation

        val rotation = DataCollection.deviceOrientation(this)
        // Check the rotation value
        if (rotation == Surface.ROTATION_0) {
            // Portrait mode
            binding.screenOrientationTv.text = getString(R.string.portraint_mode)
        } else if (rotation == Surface.ROTATION_90) {
            // Landscape mode, facing right
            binding.screenOrientationTv.text = getString(R.string.landscape_right_mode)
        } else if (rotation == Surface.ROTATION_180) {
            // Portrait mode, upside down
            binding.screenOrientationTv.text = getString(R.string.portraint_upside_mode)
        } else if (rotation == Surface.ROTATION_270) {
            // Landscape mode, facing left
            binding.screenOrientationTv.text = getString(R.string.landscape_left_mode)
        }

        // check auto net selected
        val autoNet = DataCollection.isAutoNetSelectionEnabled(this)
        if (autoNet) {
            binding.autoNetSelectedTv.text = getString(R.string.auto_net_enabled)
        } else {
            binding.autoNetSelectedTv.text = getString(R.string.auto_net_disabled)
        }
        // set auto timezone
        val autoTimeZoneEnabled = DataCollection.autoTimeZoneEnabled(this)
        if (autoTimeZoneEnabled == "1") {

            binding.autoTimeZoneTv.text = getString(R.string.enabled)
        } else {
            binding.autoTimeZoneTv.text = getString(R.string.disabled)
        }

        // check network device using
        val isConnectedToWifi = DataCollection.isWifiConnected(this)
        if (isConnectedToWifi) {
            binding.netwrokConfigTv.text = getString(R.string.wifi_network)
            // Device is connected to Wi-Fi
        } else {
            binding.netwrokConfigTv.text = getString(R.string.other_network)
            // Device is not connected to Wi-Fi
        }
        // get the mac address of device wifi network
        val macAddress = DataCollection.getMacAddress(this)
        if (macAddress != null) {
            binding.macAddressTv.text = macAddress
        } else {
            println("Failed to get MAC address")
        }

        // check closed captioning enabled or not
        val closedCaptioningEnabled = DataCollection.isClosedCaptioningEnabled(this)
        if (closedCaptioningEnabled) {
            // Closed Captioning is enabled
            binding.closedCaptioningTv.text = getString(R.string.enabled)
        } else {
            // Closed Captioning is disabled
            binding.closedCaptioningTv.text = getString(R.string.disabled)
        }
        // check TalkBack option is enabled or not
        val talkback = DataCollection.isScreenReaderOn(this)
        if (talkback) {
            binding.talkBackTv.text = getString(R.string.enabled)
        } else {
            binding.talkBackTv.text = getString(R.string.disabled)
        }

        /** last boot time of device in hours*/
        val formattedBootTime = DataCollection.getBootTime("MM/dd/yyyy HH:mm:ss")
        binding.lastbootTimeTv.text = "$formattedBootTime"
        /** UUID */
        binding.tvUUIDValue.text = DataCollection.generateUUID()
        /** Device Model  */
        val deviceModel = DataCollection.deviceModel()
        binding.tvDeviceModelValue.text = deviceModel

        /** Color Inversion  */
        val isColorInversion = DataCollection.isColorInversionEnabled(this)
        binding.tvColorInversionValue.text = isColorInversion.toString()
        /** Device Language  */
        val deviceLang = DataCollection.getDeviceLanguage(this)
        binding.tvLangValue.text = deviceLang

        /** Country Name */
        val locale = DataCollection.countryName(this)
        binding.tvCountryNameValue.text = locale
        /** CPU Type */
        val cpuAbi = DataCollection.cpuType()
        binding.tvCpuNameValue.text = cpuAbi

        /** Device Size */
        val deviceWidthAndHeight = DataCollection.getDeviceWidthAndHeight(this)
        val width = deviceWidthAndHeight.first
        val height = deviceWidthAndHeight.second
        binding.tvDeviceDimensionValue.text = height.toString() + "x" + width.toString()
        /** Kernel Name */
        val kernelName = DataCollection.getKernelVersion("uname")
        binding.tvKernelNameValue.text = kernelName
        /** Kernel Version */
        val kernelVersion = DataCollection.getKernelVersion("uname -r")
        binding.tvKernelVersionValue.text = kernelVersion

        /** Calendar Type */
        val calendarType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DataCollection.getDeviceCalendarType()
        } else {
            DataCollection.getDeviceCalendarType()
        }
        binding.calendarTypeValue.text = calendarType

        /** AppPineed */
        val isAppPinned = DataCollection.isAppPinned(this)
        if (isAppPinned) {
            binding.tvAppPinnedValue.text = getString(R.string.app_pined)
        } else {
            binding.tvAppPinnedValue.text = getString(R.string.app_not_pinned)
        }

    }


    private fun initFineLocationPermission() {
        if (permissionCheckers.isPermissionGranted(PermissionCheckers.FINE_LOCATION)) {
            setWifiSSIDName()
        } else {
            permissionCheckers.requestPermission(
                PermissionCheckers.FINE_LOCATION,
                PermissionCheckers.FINE_LOCATION_CODE
            )
        }
    }

    private fun initPhoneStatePermission() {
        if (permissionCheckers.isPermissionGranted(PermissionCheckers.PHONE_STATE)) {
            getCarrierName()
        } else {
            permissionCheckers.requestPermission(
                PermissionCheckers.PHONE_STATE,
                PermissionCheckers.PHONE_STATE_CODE
            )
        }
    }


    // set wifissid when location permission granted
    private fun setWifiSSIDName() {
        binding.tvWifiSSIDValue.text = DataCollection.getWifiSSID(this@MainActivity)

    }

    // set career name when permission granted
    private fun getCarrierName() {
        binding.tvCarrierNameValue.text = DataCollection.getAndDisplayCarrierName(this)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionCheckers.PHONE_STATE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PermissionCheckers.GRANTED) {
                getCarrierName()
            } else {
                if (!shouldShowRequestPermissionRationale(PermissionCheckers.PHONE_STATE)) {
                    DialogUtils.goToSystemLocationSetting(
                        this, PermissionCheckers.PHONE_STATE_CUSTOM_DIALOG_MESSAGE
                    )
                }
            }
        }

        if (requestCode == PermissionCheckers.FINE_LOCATION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PermissionCheckers.GRANTED) {
                setWifiSSIDName()
            } else {
                if (!shouldShowRequestPermissionRationale(PermissionCheckers.FINE_LOCATION)) {
                    DialogUtils.goToSystemLocationSetting(
                        this, PermissionCheckers.LOCATION_CUSTOM_DIALOG_MESSAGE
                    )
                }
            }
        }
    }
}