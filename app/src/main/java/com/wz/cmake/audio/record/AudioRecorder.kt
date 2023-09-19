package com.wz.cmake.audio.record

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.wz.cmake.audio.AudioRecordStatus
import com.wz.cmake.audio.ChangeBuffer
import com.wz.cmake.audio.EncodeDeleteListener
import com.wz.cmake.audio.EncodeFinishListener
import com.wz.cmake.audio.Mp3EncodeThread
import com.wz.cmake.audio.RecordDataListener
import com.wz.cmake.audio.RecordFftDataListener
import com.wz.cmake.audio.RecordResultListener
import com.wz.cmake.audio.RecordSoundSizeListener
import com.wz.cmake.audio.RecordStateListener
import com.wz.cmake.audio.fft.FftFactory
import com.wz.cmake.util.ByteUtils
import com.wz.cmake.util.FileUtil
import com.wz.cmake.util.WavUtils
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class AudioRecorder {

    val TAG = AudioRecorder::class.java.simpleName

    var mRecordSoundSizeListener: RecordSoundSizeListener? = null

    //音频输入-麦克风
    private val AUDIO_INPUT = MediaRecorder.AudioSource.MIC
    //录音对象
    private var audioRecord:AudioRecord? = null
    //缓冲区字节大小
    private var bufferSizeInBytes = 0

    //录音配置
    private var currentConfig: RecordConfig = RecordConfig()

    //录音状态
    private var audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_IDLE

    private var resultFile: File? = null

    private var tmpFile: File? = null

    private var fileName:String? = null

    private var mp3EncodeThread: Mp3EncodeThread? = null

    private var recordStateListener: RecordStateListener? = null

    private var recordDataListener: RecordDataListener? = null

    private var recordFftDataListener: RecordFftDataListener? = null

    private var recordResultListener: RecordResultListener? = null

    private val mainHandler:Handler by lazy{
        Handler(Looper.getMainLooper())
    }

    private val fftFactory: FftFactory by lazy{
        FftFactory()
    }

    private val files by lazy{
        mutableListOf<File>()
    }

    private var audioThread: AudioRecordThread? = null


    companion object{
        @JvmStatic
        private val mInstance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AudioRecorder()
        }
        @JvmStatic
        fun getInstance(): AudioRecorder{
            return mInstance
        }
    }

    init {
        prepareRecord()
    }

    fun getAudioRecordStatus():AudioRecordStatus{
        return AudioRecordStatus.AUDIO_RECORD_FINISH
    }

    fun setRecordSoundSizeListener(listener:RecordSoundSizeListener){
        mRecordSoundSizeListener = listener
    }

    fun setRecordResultListener(listener:RecordResultListener){
        recordResultListener = listener
    }

    fun setRecordConfig(config:RecordConfig){
        currentConfig = config
    }

    fun getCurrentConfig():RecordConfig{
        return currentConfig
    }

    /**
     * @param fileName 文件名
     * @param audioSource 声音来源
     * @param sampleRateInHz 采样率
     * @param channelConfig 声道配置
     * @param audioFormat 音频格式
     * 创建录音对象
     */
    @SuppressLint("MissingPermission")
    fun createAudio(fileName:String, audioSource:Int, sampleRateInHz:Int, channelConfig:Int, audioFormat:Int){
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,channelConfig, AudioFormat.ENCODING_PCM_16BIT)
        audioRecord = AudioRecord(audioSource,sampleRateInHz,channelConfig,audioFormat,bufferSizeInBytes)
        this.fileName = fileName
    }

    @SuppressLint("MissingPermission")
    fun prepareRecord() {
        //获取缓冲区字节大小
        if (bufferSizeInBytes == 0) {
            bufferSizeInBytes = AudioRecord.getMinBufferSize(currentConfig.sampleRate,currentConfig.channelConfig,currentConfig.encodingConfig)
        }
        if (audioRecord == null) {
            audioRecord = AudioRecord(AUDIO_INPUT,currentConfig.sampleRate,currentConfig.channelConfig,currentConfig.encodingConfig,bufferSizeInBytes)
        }
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_PREPARE
    }

    /**
     * 开始录音
     */
    fun startRecord(){
        fileName = FileUtil.getFilePath()
        if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_START) {
            Log.d(TAG,"正在录音")
        }
        Log.d(TAG, "===startRecord===${audioRecord?.state}")
        resultFile = File(fileName)

        val tmpFilePath = FileUtil.getTempFilePath()
        tmpFile = File(tmpFilePath)

        //开启录音线程
        audioThread = AudioRecordThread()
        audioThread?.start()
    }


    inner class AudioRecordThread: Thread(){
        init {
            if (getCurrentConfig().format == RecordFormat.MP3) {
                if (mp3EncodeThread == null) {
                    initMp3EncodeThread(bufferSizeInBytes)
                }else{
                    mp3EncodeThread?.setFile(File(fileName))
                }
            }
        }
        override fun run() {
            super.run()
            when (getCurrentConfig().format) {
                RecordFormat.MP3->{
                    startMp3Record()
                }
                else -> {
                    startPcmRecord()
                }
            }
        }
    }

    private fun startMp3Record() {
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_START
        notifyState()
        try {
            audioRecord?.startRecording()
            val byteBuffer = ShortArray(bufferSizeInBytes)
            while (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_START) {
                val end = audioRecord?.read(byteBuffer, 0, byteBuffer.size) ?:0
                if (mp3EncodeThread != null) {
                    mp3EncodeThread?.addChangeBuffer(ChangeBuffer(byteBuffer,end))
                }
                notifyData(ByteUtils.toBytes(byteBuffer))
            }
            audioRecord?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "录音失败")
        }
        if (audioRecordStatus != AudioRecordStatus.AUDIO_RECORD_PAUSE) {
            if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_CANCEL) {
                deleteMp3Encoded()
            }else{
                stopMp3Record()
            }
        }else{
            Log.e(TAG, "暂停")
        }
    }

    private fun startPcmRecord() {
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_START
        notifyState()
        Log.e(TAG, "start record PCM")
        var fos:FileOutputStream? = null
        fos = FileOutputStream(tmpFile)
        try {
            audioRecord?.startRecording()
            val byteBuffer = ByteArray(bufferSizeInBytes)
            while (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_START) {
                val readSize = audioRecord?.read(byteBuffer, 0, byteBuffer.size)?:0
                if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
                    notifyData(byteBuffer)
//                    fos.write(byteBuffer)
                    fos.write(byteBuffer, 0, readSize)
                }
                fos.flush()
            }
            audioRecord?.stop()
            tmpFile?.let {
                files.add(it)
            }
            if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_STOP) {
                makeFile()
            } else {
                Log.i(TAG, "cancel record")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "error creating record")
        }
        if (audioRecordStatus != AudioRecordStatus.AUDIO_RECORD_PAUSE) {
            audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_IDLE
            notifyState()
            Log.i(TAG, "record finished")
        }
    }

    private fun initMp3EncodeThread(bufferSizeInBytes: Int) {
        try {
            mp3EncodeThread = Mp3EncodeThread(File(fileName), bufferSizeInBytes)
            mp3EncodeThread?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.toString())
        }
    }

    private fun makeFile() {
        when (currentConfig.format) {
            RecordFormat.MP3 -> return
            RecordFormat.WAV -> {
                mergePcmFile()
                makeWav()
            }
            RecordFormat.PCM -> {
                mergePcmFile()
            }
            else->{

            }
        }
        notifyFinish()
        Log.e(TAG, "录音结束 onFinish fileSize:${resultFile?.length()} file:${resultFile?.absoluteFile}")
    }

    private fun makeWav() {
        if (!FileUtil.isFile(resultFile) || !FileUtil.isFileEmpty(resultFile)) return
        val wavHeader = WavUtils.generateWavHeader(
            resultFile?.length()?.toInt()?:0,
            currentConfig.sampleRate,
            currentConfig.getChannelCount(),
            currentConfig.getRealEncoding()
        )
        WavUtils.writeHeader(resultFile, wavHeader)
    }

    private fun mergePcmFile() {
        val mergeResult = mergePcmFiles(resultFile, files)
        if (!mergeResult) {
            Log.e(TAG, "合并失败")
        }
    }

    private fun mergePcmFiles(recordFile: File?, files: MutableList<File>): Boolean {
        if (recordFile == null || files.isNullOrEmpty()) return false
        var fos:FileOutputStream? = null
        var bos: BufferedOutputStream? = null
        val buffer = ByteArray(1024)
        try {
            fos = FileOutputStream(recordFile)
            bos = BufferedOutputStream(fos)
            files.forEach {itemFile ->
                val inputStream = BufferedInputStream(FileInputStream(itemFile))
                var readCount = 0
                while (inputStream.read(buffer).also { readCount = it } > 0) {
                    bos.write(buffer, 0, readCount)
                }
                inputStream.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "mergePcmFiles error")
            return false
        }finally {
            try {
                bos?.close()
                fos?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        files.forEach {
            it.delete()
        }
        files.clear()
        return true
    }

    private fun stopMp3Record() {
        if (mp3EncodeThread != null) {
            mp3EncodeThread!!.stopSafe(object:EncodeFinishListener{
                override fun onFinish() {
                    notifyFinish()
                    mp3EncodeThread = null
                }
            })
        }else{
            Log.e(TAG, "mp3EncodeThread is null error")
        }
    }

    private fun deleteMp3Encoded() {
        if (mp3EncodeThread != null) {
            mp3EncodeThread?.deleteSafe(object : EncodeDeleteListener {
                override fun onDelete() {
                    mp3EncodeThread = null
                }
            })
        } else {
            Log.e(TAG, "mp3EncodeThread is null error")
        }
    }


    private fun notifyFinish() {
        Log.e(TAG, "录音结束 onFinish file:${resultFile?.absoluteFile}")
        mainHandler.post {
            recordStateListener?.onStateChanged(AudioRecordStatus.AUDIO_RECORD_FINISH)
            recordResultListener?.onResult(resultFile)
        }

    }

    private fun notifyData(data: ByteArray) {
        if (recordDataListener == null && mRecordSoundSizeListener == null && recordFftDataListener == null) return
        mainHandler.post {
            recordDataListener?.onData(data)
            val fftData = fftFactory.makeFftData(data)
            if (fftData != null) {
                mRecordSoundSizeListener?.onSoundSize(ByteUtils.getAve(fftData))
                recordFftDataListener?.onFftData(fftData)
            }
        }
    }

    private fun notifyState() {
        if (recordStateListener == null) return

        mainHandler.post{
            recordStateListener?.onStateChanged(audioRecordStatus)
        }

        if (audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_STOP || audioRecordStatus == AudioRecordStatus.AUDIO_RECORD_PAUSE) {
            mRecordSoundSizeListener?.onSoundSize(0)
        }
    }

    /**
     * 暂停录音
     */
    fun pauseRecord() {
        Log.d(TAG, "===pauseRecord===")
        if (audioRecordStatus !== AudioRecordStatus.AUDIO_RECORD_START) {
            Log.d(TAG, "没有在录音")
        } else {
            audioRecord!!.stop()
            audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_PAUSE
            notifyState()
        }
    }

    /**
     * 停止录音
     */
    fun stopRecord() {
        Log.d(TAG, "===stopRecord===")
        if (audioRecordStatus === AudioRecordStatus.AUDIO_RECORD_IDLE || audioRecordStatus === AudioRecordStatus.AUDIO_RECORD_PREPARE) {
            Log.d(TAG, "录音尚未开始")
        } else {
            audioRecord!!.stop()
            audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_STOP
            notifyState()
        }
    }

    /**
     * 取消录音
     */
    fun cancelRecord() {
        Log.d(TAG, "===cancelRecord===")
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_CANCEL
        notifyState()
    }

    /**
     * 销毁(释放)录音实例
     */
    fun releaseRecord() {
        Log.d(TAG, "===releaseRecord===")
        if (audioRecord != null) {
            audioRecord!!.release()
            audioRecord = null
        }
        audioRecordStatus = AudioRecordStatus.AUDIO_RECORD_IDLE
        notifyState()
    }

}