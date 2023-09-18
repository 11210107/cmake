package com.wz.cmake.audio

import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.Collections
import java.util.LinkedList

class Mp3EncodeThread() : Thread() {
    val TAG = Mp3EncodeThread::class.java.simpleName

    private val cacheBufferList:MutableList<ChangeBuffer> by lazy {
        Collections.synchronizedList(LinkedList<ChangeBuffer>())
    }

    private val lock = java.lang.Object()

    private var file: File? = null

    private var os:FileOutputStream? = null

    private var mp3Buffer:ByteArray? = null

    private var encodeFinishListener:EncodeFinishListener? = null

    private var encodeDeleteListener:EncodeDeleteListener? = null


    //是否停止录音
    @Volatile
    private var isOver = false

    //是否继续轮训数据队列
    @Volatile
    private var start = true

    constructor(file:File,bufferSize:Int) : this(){
        this.file = file
        val size = (7200 + (bufferSize * 2 * 1.25)).toInt()
        mp3Buffer = ByteArray(size)
        val currentConfig = AudioRecordManager.getInstance().getCurrentConfig()
        val sampleRate = currentConfig.sampleRate
        Log.i(
            TAG,
            "format:${currentConfig.format} in_sample_rate:${sampleRate} channelCount:${currentConfig.getChannelCount()} out_sample_rate:$sampleRate 位深:${currentConfig.getRealEncoding()}"
        )
        Mp3Encoder.init(sampleRate,currentConfig.getChannelCount(),sampleRate,currentConfig.getRealEncoding())
    }

    fun setFile(file:File){
        this.file = file
    }

    override fun run() {
        try {
            os = FileOutputStream(file)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open:${e.message}")
            return
        }
        while (start) {
            val next = next()
            val log = next?.readSize?.toString() ?: "null"
            Log.i(TAG, "处理数据：$log")
            lameData(next)
        }

    }


    fun addChangeBuffer(changeBuffer: ChangeBuffer?){
        if (changeBuffer != null) {
            cacheBufferList.add(changeBuffer)
            synchronized(lock){
                lock.notify()
            }
        }
    }

    fun stopSafe(encodeFinishListener: EncodeFinishListener) {
        this.encodeFinishListener = encodeFinishListener
        isOver = true
        synchronized(lock){
            lock.notify()
        }
    }

    fun deleteSafe(encodeDeleteListener: EncodeDeleteListener) {
        this.encodeDeleteListener = encodeDeleteListener
        synchronized(lock){
            delete()
        }
    }

    private fun lameData(buffer: ChangeBuffer) {
        if (buffer == null) return
        val rawData = buffer.rawData
        val readSize = buffer.readSize
        if (readSize > 0) {
            mp3Buffer?.let {
                val encodeSize = Mp3Encoder.encode(rawData, rawData, readSize, it)
                if (encodeSize < 0) {
                    Log.i(TAG,"Lame encoded size: $encodeSize")
                }
                try {
                    os?.write(mp3Buffer, 0, encodeSize)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }

        }

    }

    private fun next():ChangeBuffer {
        while (true) {
            if (cacheBufferList == null || cacheBufferList.isEmpty()) {
                try {
                    if (isOver) {
                        finish()
                    }
                    synchronized(lock) {
                        lock.wait()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }

            } else {
                return cacheBufferList.removeAt(0)
            }
        }
    }

    private fun finish() {
        start = false
        mp3Buffer?.let {
            val result = Mp3Encoder.flush(it)
            if (result > 0) {
                try {
                    os?.write(mp3Buffer, 0, result)
                    os?.close()
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
            Log.i(TAG, "转换结束")
        }
        encodeFinishListener?.onFinish()
    }

    private fun delete() {
        if (file != null) {
            val delete = file?.delete()
            Log.i(TAG, "result:$delete")
        }
    }
}