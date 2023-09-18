package com.wz.cmake

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.wz.cmake.audio.AudioRecordManager
import com.wz.cmake.audio.record.RecordConfig
import com.wz.cmake.audio.record.RecordFormat
import com.wz.cmake.databinding.ActivityMainBinding
import com.wz.cmake.util.APPUtil
import org.fmod.FMOD
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object {
        // Used to load the 'cmake' library on application startup.
        init {
            System.loadLibrary("fmod")
            System.loadLibrary("fmodL")
            System.loadLibrary("cmake")
        }
    }

    private val MODE_NORMAL = 0
    private val MODE_LOLITA = 1
    private val MODE_UNCLE = 2
    private val MODE_THRILLER = 3
    private val MODE_FUNNY = 4
    private val MODE_ETHEREAL = 5
    private val MODE_CHORUS = 6
    private val MODE_TREMOLO = 7
    private lateinit var binding: ActivityMainBinding
    private val singing: AssetFileDescriptor by lazy {
        assets.openFd("singing.wav")
    }
    val path = "file:///android_asset/singing.wav"
    var path2 = "file:///android_asset/alipay.mp3"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FMOD.init(this)
        permissionRequest()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initview()
        initRecordConfig()
    }

    private fun initRecordConfig() {
        val sampleRate = 44100
        val recordConfig = RecordConfig(RecordFormat.WAV, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,sampleRate)
        AudioRecordManager.getInstance().setCurrentConfig(recordConfig)
    }

    private fun initview() {
        binding.audioRecordView.setPhoneWidth(this,APPUtil.getDisplayWidth(this))
        binding.btnLocal.setOnClickListener {
            voiceChangeNative(
                MODE_UNCLE,
                File(Environment.getExternalStorageDirectory(), "alipay.mp3").path
            )
    //            mediaPlay()
        }
        binding.btnAsset.setOnClickListener {
            voiceChangeNative(MODE_UNCLE, path2)
        }
        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()
    }

    private fun mediaPlay() {
        val mp = MediaPlayer() //构建MediaPlayer对象
        val assetFileDescriptor: AssetFileDescriptor = assets.openFd("singing.wav")
        mp.setDataSource(
            assetFileDescriptor.fileDescriptor,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.length
        ) //设置文件路径
//        val mp3 =  File(Environment.getExternalStorageDirectory(),"alipay.mp3")
//        mp.setDataSource(mp3.path)
        mp.prepare() //准备
        mp.start() //开始播放
    }

    private fun permissionRequest() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                    this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO)
            ActivityCompat.requestPermissions(this, permissions,1)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    /**
     * A native method that is implemented by the 'cmake' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String



    // 给C++调用的函数
    private fun playerEnd(msg: String) {
        Toast.makeText(this, "" + msg, Toast.LENGTH_SHORT).show()
    }

    private external fun voiceChangeNative(modeNormal: Int, path: String)

    override fun onDestroy() {
        super.onDestroy()
        FMOD.close()
    }
}