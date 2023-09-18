package com.wz.cmake.util

import android.util.Log
import java.io.File
import java.io.RandomAccessFile

class WavUtils {

    companion object{
        val TAG = WavUtils::class.java.simpleName

        @JvmStatic
        fun generateWavHeader(
            totalAudioLength: Int,
            sampleRate: Int,
            channels: Int,
            sampleBits: Int,
        ): ByteArray {
            val wavHeader = WavHeader(totalAudioLength, sampleRate, channels.toShort(), sampleBits.toShort())
            return wavHeader.header
        }

        @JvmStatic
        fun writeHeader(file: File?, header: ByteArray) {
            if (!FileUtil.isFile(file))return
            var wavRaf: RandomAccessFile? = null
            try {
                wavRaf = RandomAccessFile(file, "rw")
                wavRaf.seek(0)
                wavRaf.write(header)
//                wavRaf.close()
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            } finally {
                try {
                    wavRaf?.close()
                } catch (e: Exception) {
                    Log.e(TAG, "${e.message}")
                }
            }
        }

    }

    class WavHeader internal constructor(
        var riffChunkSize: Int,
        var sampleRate: Int,
        var channels: Short,
        sampleBits: Short,
    ) {
        /**
         * RIFF数据块
         */
        val riffChunkId = "RIFF"
        val riffType = "WAVE"

        /**
         * FORMAT 数据块
         */
        val formatChunkId = "fmt "
        val formatChunkSize = 16
        val audioFormat: Short = 1
        var byteRate: Int
        var blockAlign: Short
        var sampleBits: Short

        /**
         * FORMAT 数据块
         */
        val dataChunkId = "data"
        var dataChunkSize: Int

        init {
            byteRate = sampleRate * sampleBits / 8 * channels
            blockAlign = (channels * sampleBits / 8).toShort()
            this.sampleBits = sampleBits
            dataChunkSize = riffChunkSize - 44
        }

        val header: ByteArray
            get() {
                var result: ByteArray
                result = ByteUtils.merger(
                    ByteUtils.toBytes(riffChunkId),
                    ByteUtils.toBytes(riffChunkSize)
                )
                result = ByteUtils.merger(result, ByteUtils.toBytes(riffType))
                result = ByteUtils.merger(result, ByteUtils.toBytes(formatChunkId))
                result = ByteUtils.merger(result, ByteUtils.toBytes(formatChunkSize))
                result = ByteUtils.merger(result, ByteUtils.toBytes(audioFormat))
                result = ByteUtils.merger(result, ByteUtils.toBytes(channels))
                result = ByteUtils.merger(result, ByteUtils.toBytes(sampleRate))
                result = ByteUtils.merger(result, ByteUtils.toBytes(byteRate))
                result = ByteUtils.merger(result, ByteUtils.toBytes(blockAlign))
                result = ByteUtils.merger(result, ByteUtils.toBytes(sampleBits))
                result = ByteUtils.merger(result, ByteUtils.toBytes(dataChunkId))
                result = ByteUtils.merger(result, ByteUtils.toBytes(dataChunkSize))
                return result
            }
    }

}