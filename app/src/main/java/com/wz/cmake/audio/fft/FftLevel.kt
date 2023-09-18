package com.wz.cmake.audio.fft

enum class FftLevel {
    /**
     * 原始数据，不做任何优化
     */
    ORIGINAL,
    /**
     * 对音乐进行优化
     */
    MUSIC,
    /**
     * 对人声进行优化
     */
    PEOPLE,
    /**
     * 极限优化
     */
    MAXIMAL
}