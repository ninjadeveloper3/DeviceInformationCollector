package com.device.informationcollector

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }
}