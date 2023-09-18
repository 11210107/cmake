package com.wz.cmake.audio

import com.wz.cmake.audio.record.AudioRecorder
import com.wz.cmake.audio.record.RecordConfig

class AudioRecordManager {

    companion object{
        @JvmStatic
        private val mInstance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioRecordManager()
        }
        @JvmStatic
        fun getInstance(): AudioRecordManager{
            return mInstance
        }
    }

    //录音音量监听回调
    fun setRecordSoundSizeListener(listener: RecordSoundSizeListener) {
        AudioRecorder.getInstance().setRecordSoundSizeListener(listener)
    }


    fun setStatus(currentStatus: AudioRecordStatus) {
        when (currentStatus) {
            AudioRecordStatus.AUDIO_RECORD_IDLE->{
            }
            AudioRecordStatus.AUDIO_RECORD_PREPARE->{
                AudioRecorder.getInstance().prepareRecord()
            }
            AudioRecordStatus.AUDIO_RECORD_START->{
                AudioRecorder.getInstance().startRecord()
            }
            AudioRecordStatus.AUDIO_RECORD_PAUSE->{
                AudioRecorder.getInstance().pauseRecord()
            }
            AudioRecordStatus.AUDIO_RECORD_STOP->{
                AudioRecorder.getInstance().stopRecord()
            }
            AudioRecordStatus.AUDIO_RECORD_CANCEL->{
                AudioRecorder.getInstance().cancelRecord()
            }
            AudioRecordStatus.AUDIO_RECORD_RELEASE->{
                AudioRecorder.getInstance().releaseRecord()
            }
            else -> {

            }
        }

    }

    fun getStatus(): AudioRecordStatus{
        return AudioRecorder.getInstance().getAudioRecordStatus()
    }

    fun getCurrentConfig():RecordConfig{
        return AudioRecorder.getInstance().getCurrentConfig()
    }

    fun setCurrentConfig(config: RecordConfig) {
        AudioRecorder.getInstance().setRecordConfig(config)
    }
}