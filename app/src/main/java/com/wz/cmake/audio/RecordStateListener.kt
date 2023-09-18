package com.wz.cmake.audio

interface RecordStateListener {
    fun onStateChanged(state: AudioRecordStatus)

    fun onError(error:String)
}