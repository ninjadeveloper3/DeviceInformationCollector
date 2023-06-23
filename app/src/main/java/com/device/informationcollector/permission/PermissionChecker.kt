package com.device.informationcollector.permission

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionCheckers(private val activity: Activity) {

    fun isPermissionGranted(permission : String): Boolean {
        val mPermission = ContextCompat.checkSelfPermission(activity, permission)
        if (mPermission != GRANTED) {
            return false
        }
            return true

    }

    fun requestPermission(permission : String , requestCode : Int) {
        val mPermission = ContextCompat.checkSelfPermission(activity, permission)
       if (mPermission != GRANTED) {
           ActivityCompat.requestPermissions(
               activity,
               arrayOf(
                   permission
               ),
               requestCode
           )
        }
    }

    fun isLocationPermissionGranted(): Boolean {
        val finePermission = ContextCompat.checkSelfPermission(activity, FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(activity, COARSE_LOCATION)
        if (finePermission != GRANTED && coarsePermission != GRANTED) {
            return false
        }
        return true
    }

    fun requestLocationPermission() {
        val finePermission = ContextCompat.checkSelfPermission(activity, FINE_LOCATION)
        val coarsePermission = ContextCompat.checkSelfPermission(activity, COARSE_LOCATION)
        if (finePermission != GRANTED && coarsePermission != GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    FINE_LOCATION,
                    COARSE_LOCATION
                ),
                LOCATION_CODE
            )
        }
    }

/*    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_CONTACTS_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == GRANTED) {
                retrieveContacts()
            } else {
                if (!shouldShowRequestPermissionRationale(READ_CONTACTS)) {
                    DialogUtils.goToSystemLocationSetting(
                        this@ContactListActivity, CONTACT_CUSTOM_DIALOG
                    )
                }
            }
        }
    }*/

    companion object{

        const val GRANTED = PackageManager.PERMISSION_GRANTED

        /** Read Contact */
        const val READ_CONTACTS = android.Manifest.permission.READ_CONTACTS
        const val READ_CONTACTS_CODE = 100
        const val CONTACT_CUSTOM_DIALOG_MESSAGE = "Contact permission required"

        /** Bluetooth / Near by Devices */
        const val BLUETOOTH = Manifest.permission.BLUETOOTH
        const val BLUETOOTH_CODE = 101
        @RequiresApi(Build.VERSION_CODES.S)
        const val BLUETOOTH_CONNECT = Manifest.permission.BLUETOOTH_CONNECT
        const val BLUETOOTH_CONNECT_CODE = 102
        const val BLUETOOTH_CUSTOM_DIALOG = "Bluetooth permission required"

        /** Phone State / Carrier Name */
        const val PHONE_STATE = android.Manifest.permission.READ_PHONE_STATE
        const val PHONE_STATE_CODE = 103
        const val PHONE_STATE_CUSTOM_DIALOG_MESSAGE = "Phone permission required"

        /** Location Fine / Wifi SSID */
        const val FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
        const val FINE_LOCATION_CODE = 104
        const val LOCATION_CUSTOM_DIALOG_MESSAGE = "Location permission required"

        /** Location Coarse */
        const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val COARSE_LOCATION_CODE = 105
        const val LOCATION_CODE = 106

        /** Read Calendar */
        const val READ_CALENDAR = android.Manifest.permission.READ_CALENDAR
        const val READ_CALENDAR_CODE = 107
        const val READ_CALENDAR_CUSTOM_DIALOG_MESSAGE = "Calendar permission required"


        /** Read Contact */
        const val EXTERNAL_STORAGE = android.Manifest.permission.READ_EXTERNAL_STORAGE
        const val EXTERNAL_STORAGE_CODE = 108
        const val EXTERNAL_STORAGE_CUSTOM_DIALOG_MESSAGE = "Storage permission required"

    }

}