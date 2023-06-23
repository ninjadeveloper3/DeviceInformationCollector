package com.device.informationcollector.contact

import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import com.device.deviceinformationlibrary.DataCollection
import com.device.deviceinformationlibrary.models.Contact
import com.device.informationcollector.databinding.ActivityContactListBinding
import com.device.informationcollector.dialog.DialogUtils
import com.device.informationcollector.permission.PermissionCheckers
import com.device.informationcollector.permission.PermissionCheckers.Companion.CONTACT_CUSTOM_DIALOG_MESSAGE
import com.device.informationcollector.permission.PermissionCheckers.Companion.GRANTED
import com.device.informationcollector.permission.PermissionCheckers.Companion.READ_CONTACTS
import com.device.informationcollector.permission.PermissionCheckers.Companion.READ_CONTACTS_CODE

class ContactListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactListBinding

    private val permissionChecker by lazy { PermissionCheckers(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initContactPermission()

    }


    private fun initContactPermission() {
        if (permissionChecker.isPermissionGranted(READ_CONTACTS)) {
            retrieveContacts()
        } else {
            permissionChecker.requestPermission(READ_CONTACTS, READ_CONTACTS_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionChecker.isPermissionGranted(READ_CONTACTS)) {
            retrieveContacts()
        }
    }

    private fun retrieveContacts() {
        val contactsList = DataCollection.deviceContactsList(this)

//        // Set up the contact query
//        val projection = arrayOf(
//            ContactsContract.Contacts.DISPLAY_NAME,
//            ContactsContract.CommonDataKinds.Phone.NUMBER
//        )
//        val selection = null
//        val selectionArgs = null
//        val sortOrder = null
//
//        // Perform the query
//        val cursor = contentResolver.query(
//            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//            projection,
//            selection,
//            selectionArgs,
//            sortOrder
//        )
//
//        cursor?.use { cursor ->
//            val nameColumnIndex =
//                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
//            val phoneColumnIndex =
//                cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
//
//            while (cursor.moveToNext()) {
//                val name = cursor.getString(nameColumnIndex)
//                val phoneNumber = cursor.getString(phoneColumnIndex)
//
//                val contact = Contact(name, phoneNumber)
//                contactsList.add(contact)
//            }
//        }
//
//
//        // Close the cursor after use
//        cursor?.close()

        // Process the retrieved contacts as needed
        processContacts(contactsList)
    }

    private fun processContacts(contactsList: List<Contact>) {
        val stringBuilder = StringBuilder()
        for (contact in contactsList) {
            stringBuilder.append(contact.name + ":" + contact.phone + "\n")
        }
        binding.tvContactList.text = stringBuilder
    }

    override fun onRequestPermissionsResult(
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
                        this@ContactListActivity, CONTACT_CUSTOM_DIALOG_MESSAGE
                    )
                }
            }
        }
    }

}
