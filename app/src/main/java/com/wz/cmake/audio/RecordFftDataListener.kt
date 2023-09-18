package com.wz.cmake.audio

interface RecordFftDataListener {
    //录音可视化数据：傅里叶转换后的数据
    fun onFftData(data:ByteArray)
}