package com.device.informationcollector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.device.deviceinformationlibrary.getTimeStamp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getTimeStamp()
    }

}