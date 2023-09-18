package com.wz.cmake.audio

interface RecordDataListener {
    //当前音频数据
    fun onData(data: ByteArray)

}