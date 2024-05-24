package com.wz.cmake

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.media.AudioFormat
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.wz.cmake.api.AudioServiceImpl
import com.wz.cmake.api.EmailService
import com.wz.cmake.api.IAudioService
import com.wz.cmake.api.OrderService
import com.wz.cmake.audio.AudioRecordManager
import com.wz.cmake.audio.Mp3Encoder
import com.wz.cmake.audio.RecordResultListener
import com.wz.cmake.audio.record.RecordConfig
import com.wz.cmake.audio.record.RecordFormat
import com.wz.cmake.databinding.ActivityMainBinding
import com.wz.cmake.model.Order
import com.wz.cmake.ui.JSBridgeActivity
import com.wz.cmake.util.APPUtil
import com.wz.cmake.util.ContextUtil
import com.wz.cmake.util.DateUtil
import com.wz.cmake.util.FileUtil
import com.wz.cmake.util.GsonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.fmod.FMOD
import java.io.File
import java.lang.reflect.Proxy
import java.util.Locale


class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.simpleName
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
    var soundMode = 0
    private lateinit var binding: ActivityMainBinding
    private val singing: AssetFileDescriptor by lazy {
        assets.openFd("singing.wav")
    }
    private var recordFile:File? = null

    val soundAdapter:ArrayAdapter<String> by lazy {
        val items = mutableListOf<String>("正常","萝莉","大叔","惊悚","搞怪","空灵","和声","颤音")
        ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,items)
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
        val recordConfig = RecordConfig(RecordFormat.PCM, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,sampleRate)
        AudioRecordManager.getInstance().setCurrentConfig(recordConfig)
        AudioRecordManager.getInstance().setRecordResultListener(object : RecordResultListener{
            override fun onResult(result: File?) {
                recordFile = result
                playRecordAudio()
                pcm2mp3()
            }
        })
    }

    //输出MP3的码率
    private val BIT_RATE = 16
    private fun pcm2mp3() {
        val fileDir = String.format(
            Locale.getDefault(),
            "%s/Record/",
            ContextUtil.get()?.dataDir?.absoluteFile
        )
        val fileName = String.format(
            Locale.getDefault(),
            "lame_encode_%s",
            DateUtil.millis2String(System.currentTimeMillis(), DateUtil.SIMPLE_DATE_FORMAT)
        )
        val mp3File = File(String.format(Locale.getDefault(), "%s%s.mp3", fileDir, fileName))
        val mp3Encoder = Mp3Encoder()
        mp3Encoder.encodeMp3(
            recordFile?.absolutePath ?: "",
            mp3File.absolutePath,
            44100,
            1,
            BIT_RATE
        )
    }

    private fun playRecordAudio() {
        MainScope().launch {
            withContext(Dispatchers.IO) {
                voiceChangeNative(
                    soundMode,
                    recordFile?.absolutePath?:path2
                )
            }
        }
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
            voiceChangeNative(0, path)
        }
        // Example of a call to a native method
        binding.sampleText.text = stringFromJNI()
        binding.sampleText.setOnClickListener {
            val mp3Encoder = Mp3Encoder()
            val result = mp3Encoder.doAction("mp3Encoder")
            Log.e(TAG, "result:$result")
        }

        binding.soundModeSpinner.adapter = soundAdapter

        binding.soundModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                soundMode = p2
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }

        binding.btnDynamicAgent.setOnClickListener {
            val impl: UserProxyService = Proxy.newProxyInstance(
                UserProxyService::class.java.classLoader,
                arrayOf<Class<*>>(
                    UserProxyService::class.java
                )
            ) { proxy, method, args ->
                Log.e(TAG, "invoke method:$method args:$args")
//                method.invoke(impl,args)
                Log.e(TAG, "invoke method end")
                null
            } as UserProxyService
            Log.e(TAG,"impl")
            impl.update()
        }

        binding.btnProxyClass.setOnClickListenerProxy(object :OnClickListenerProxy{
            override fun onClick(v: View) {
                Log.e(TAG,"create class code")
                val recordService = createLoggerProxy(AudioServiceImpl()) as IAudioService
                recordService.record()
            }
        })
        val monitor = object :PerformanceMonitor{
            override fun startMonitor() {
                Log.e(TAG,"startMonitor")
            }

            override fun stopMonitor() {
                Log.e(TAG,"stopMonitor")
            }

            override fun logPerformance(methodName: String, executionTime: Long) {
                Log.e(TAG,"methodName:$methodName executed in $executionTime ms")
            }

        }
        binding.btnMethodTime.setOnClickListenerProxy(object :OnClickListenerProxy{
            override fun onClick(v: View) {
//                val monitorProxy = createPerformanceMonitorProxy(MonitorApiImpl(),monitor) as IMonitorApi
//                monitorProxy.execute()

                val runnable = createRunnableMonitorProxy(Runnable {
                    Log.e(TAG, "thread start")
                     Thread.sleep(5000L)
                    Log.e(TAG, "thread end")
                },monitor) as Runnable
                Thread(runnable).start()
            }

        })

        binding.btnGson.setOnClickListenerProxy(object :OnClickListenerProxy{
            override fun onClick(v: View) {
                GsonUtil.serialization()
                GsonUtil.deserialization()
                GsonUtil.user2Json()
                GsonUtil.json2User()
                val order = Order("123456",0,"123@gmail.com","12345678901")
                val email = EmailService()
                OrderService(email).notifyOrderShipped(order)
            }
        })
        binding.btnJsBridge.setOnClickListenerProxy(object :OnClickListenerProxy{
            override fun onClick(v: View) {
                startActivity(Intent(this@MainActivity,JSBridgeActivity::class.java))
            }

        })
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
        MainScope().launch {
            Toast.makeText(this@MainActivity, "" + msg, Toast.LENGTH_SHORT).show()
            withContext(Dispatchers.IO) {
                delay(5000)
                FileUtil.deleteFile(recordFile)
            }
        }
    }

    private external fun voiceChangeNative(modeNormal: Int, path: String)

    override fun onDestroy() {
        super.onDestroy()
        FMOD.close()
    }
}