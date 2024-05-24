package com.wz.cmake.api

import android.util.Log

class MonitorApiImpl:IMonitorApi {
    override fun execute() {
        Log.d("MonitorApiImpl", "Monitor execute")
        Thread.sleep(1000L)
    }
}