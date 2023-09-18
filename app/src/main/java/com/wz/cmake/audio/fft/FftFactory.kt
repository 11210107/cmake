package com.wz.cmake.audio.fft

import android.util.Log
import com.wz.cmake.util.ByteUtils

class FftFactory {
    val TAG = FftFactory::class.java.simpleName

    private var level:FftLevel = FftLevel.ORIGINAL

    fun makeFftData(pcmData: ByteArray):ByteArray?{
        if (pcmData.size < 1024) {
            Log.e(TAG, "makeFftData")
            return null
        }
        val doubles = ByteUtils.toHardDouble(ByteUtils.toShorts(pcmData))
        val fft = FFT.fft(doubles, 0)
        if (level == FftLevel.ORIGINAL) {
            return ByteUtils.toSoftBytes(fft)
        }else{
            return ByteUtils.toHardBytes(fft)
        }
    }

    fun isSimpleData(data: ByteArray, i: Int):Boolean {
        val start = Math.max(0, i - 5)
        val end = Math.min(data.size, i + 5)

        var max: Byte = 0
        var min: Byte = 127
        for (j in start until end) {
            if (data[j] > max) {
                max = data[j]
            }
            if (data[j] < min) {
                min = data[j]
            }
        }
        return data[i] == min || data[i] == max
    }
}