package com.wz.cmake.audio

enum class AudioRecordStatus {
    //未开始
    AUDIO_RECORD_IDLE,
    //初始化
    AUDIO_RECORD_PREPARE,
    //录音
    AUDIO_RECORD_START,
    //暂停
    AUDIO_RECORD_PAUSE,
    //停止
    AUDIO_RECORD_STOP,
    //录音流程结束（转换结束）
    AUDIO_RECORD_FINISH,
    //取消
    AUDIO_RECORD_CANCEL,
    //释放
    AUDIO_RECORD_RELEASE
}