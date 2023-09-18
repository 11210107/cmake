package com.wz.cmake.audio

import java.io.File

interface RecordResultListener {
    fun onResult(result: File?)
}