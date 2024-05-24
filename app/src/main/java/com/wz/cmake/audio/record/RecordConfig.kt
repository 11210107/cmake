package com.wz.cmake.audio.record

import android.media.AudioFormat
import com.wz.cmake.util.ContextUtil

class RecordConfig() {
    val TAG: String = RecordConfig::class.java.simpleName
    //录音格式，默认WAV格式
    var format = RecordFormat.MP3
    //通道数：默认单通道
    var channelConfig = AudioFormat.CHANNEL_IN_MONO
    //位宽 单位bit
    var encodingConfig = AudioFormat.ENCODING_PCM_16BIT
        /*get(){
            if (format == RecordFormat.MP3){
               return 16
            }
            return when (channelConfig) {
                AudioFormat.ENCODING_PCM_8BIT -> {
                    8
                }
                AudioFormat.ENCODING_PCM_16BIT -> {
                    16
                }
                else -> {
                    0
                }
            }
        }*/


    //采样率
    var sampleRate:Int  = 44100
    //录音文件存放路径
//    private var recordDir = "${Environment.getExternalStorageDirectory().absoluteFile}/Record/"
    private var recordDir = "${ContextUtil.get()?.dataDir}/Record/"

    constructor(format:RecordFormat):this(){
        this.format = format
    }

    /**
     * @param format 录音文件格式
     * @param channelConfig 声道配置
     * @param encodingConfig 位深配置
     * @param sampleRate 采样率
     */
    constructor(format: RecordFormat,channelConfig:Int,encodingConfig:Int,sampleRate:Int):this(format){
        this.format = format
        this.channelConfig = channelConfig
        this.encodingConfig = encodingConfig
        this.sampleRate = sampleRate
    }
    fun getRecordDir():String {
        return this.recordDir
    }

    /**
     * @return 采样位深 单位bit
     */
    fun getRealEncoding():Int {
        return when (encodingConfig) {
            AudioFormat.ENCODING_PCM_8BIT -> {
                8
            }
            AudioFormat.ENCODING_PCM_16BIT -> {
                16
            }
            else -> {
                0
            }
        }
    }
    /**
     * @return 声道数：0error
     */
    fun getChannelCount(): Int{
        return when (channelConfig) {
            AudioFormat.CHANNEL_IN_MONO -> 1
            AudioFormat.CHANNEL_IN_STEREO -> 2
            else -> 0
        }
    }

    override fun toString(): String {
        return "录制格式：${format}，采样率：${sampleRate}，位深：${encodingConfig}，声道数：${getChannelCount()}"
    }
}