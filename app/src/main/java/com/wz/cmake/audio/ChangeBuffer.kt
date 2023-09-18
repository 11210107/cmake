package com.wz.cmake.audio

class ChangeBuffer(var rawData: ShortArray, var readSize: Int) {
    init {
        this.rawData = rawData.clone()
        this.readSize = readSize
    }
}