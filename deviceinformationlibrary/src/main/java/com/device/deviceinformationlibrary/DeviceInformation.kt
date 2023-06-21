package com.device.deviceinformationlibrary

fun getTimeStamp(): String {
    // current time in milis
    val currentTimeMillis = System.currentTimeMillis()
    //timestamp
    val timeStamp = java.sql.Timestamp(currentTimeMillis)

    return timeStamp.toString()
}
