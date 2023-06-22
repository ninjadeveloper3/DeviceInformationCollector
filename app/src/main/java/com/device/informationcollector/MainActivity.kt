package com.device.informationcollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import com.device.deviceinformationlibrary.DataCollection
import com.device.deviceinformationlibrary.getTimeStamp
import com.device.informationcollector.databinding.ActivityMainBinding
import com.device.informationcollector.databinding.ActivityMainLocationBinding
import com.device.informationcollector.location.MainLocationActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setViews()

    }
    private fun initViews(){

    }

    private fun setViews(){
        // set current timestamp
        binding.timeStampTv.text= DataCollection.getTimeStamp()
        // set device name
        binding.deviceNameTv.text= DataCollection.getDeviceName()
        // set manufacturer name
        binding.manufacturerNameTv.text= DataCollection.getDeviceManufacturerName()
        // set OS version
        binding.versionOsTv.text= DataCollection.getDeviceAndroidOSVersion()
        // set build version
        binding.buildVersionTv.text= DataCollection.getDeviceBuildVersion().toString()
        // set device ip over network
        binding.deviceIpTv.text= DataCollection.getIpAddress()
        // set android device id
        binding.androidDeviceIDTv.text= DataCollection.getAndroidDeviceId(this)
        // set device cpu clock speed
        binding.cpuClockSPeedTv.text= DataCollection.getDeviceCpuClockSpeed()
        // set available internal storage
        binding.availableInternalStorageTv.text= DataCollection.getAvailableInternalStorageInGB().toString()
    }
}