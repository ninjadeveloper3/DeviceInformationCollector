package com.device.informationcollector.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    private const val TAG = "DialogUtils"

    fun goToSystemLocationSetting(activity: Activity, permissionMessage: String) {
        val alertDialog = AlertDialog.Builder(activity).create()
        alertDialog.setTitle("Permission")
        alertDialog.setMessage(
            permissionMessage
        )
        alertDialog.setCancelable(false)
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Setting"
        ) { dialog: DialogInterface, which: Int ->
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", activity.packageName, null)
            intent.data = uri
            activity.startActivity(intent)
            dialog.dismiss()
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "Cancel"
        ) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
}