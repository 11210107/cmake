package com.wz.cmake.api

import android.util.Log
import com.wz.cmake.Loggable

class AudioServiceImpl:IAudioService,Loggable {
    val TAG = AudioServiceImpl::class.java.simpleName
    override fun logBefore(methodName: String) {
        Log.e(TAG,"log before record")
    }

    override fun logAfter(methodName: String) {
        Log.e(TAG,"log after record")
    }

    override fun record() {
        Log.e(TAG,"record")
    }
}