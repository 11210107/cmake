package com.wz.cmake.audio

class Mp3Encoder {
    init {
        System.loadLibrary("mp3lame")
    }
    companion object{
        @JvmStatic
        external fun close()

        @JvmStatic
        external fun encode(buffer_l:ShortArray,buffer_r:ShortArray,samples:Int,mp3buf:ByteArray):Int

        @JvmStatic
        external fun flush(mp3buf:ByteArray):Int

        @JvmStatic
        external fun init(inSampleRate:Int,outChannel:Int,outSampleRate:Int,outBitrate:Int,quality:Int)

        @JvmStatic
        fun init(inSampleRate:Int,outChannel:Int,outSampleRate:Int,outBitrate:Int){
            init(inSampleRate, outChannel, outSampleRate, outBitrate,7)
        }

    }

}