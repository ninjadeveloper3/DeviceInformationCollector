package com.chatgpt.masterclass.datacollectionlibrary.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var mContext: Context

        fun getContext(): Context {
            return mContext
        }
    }
}