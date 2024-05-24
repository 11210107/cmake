package com.wz.cmake.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.github.lzyzsd.jsbridge.BridgeHandler
import com.github.lzyzsd.jsbridge.CallBackFunction
import com.github.lzyzsd.jsbridge.DefaultHandler
import com.google.gson.Gson
import com.wz.cmake.databinding.ActivityJsBridgeBinding
import com.wz.cmake.model.Gender
import com.wz.cmake.model.User

class JSBridgeActivity : AppCompatActivity() {
    val TAG = JSBridgeActivity::class.java.simpleName
    lateinit var binding: ActivityJsBridgeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJsBridgeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.run {

            btnJavaToJs.setOnClickListener {
                bridgeWebView.send("hello")
            }

            bridgeWebView.setWebChromeClient(object : WebChromeClient() {
                @Suppress("unused")
                fun openFileChooser(
                    uploadMsg: ValueCallback<Uri?>,
                    AcceptType: String?,
                    capture: String?
                ) {
                    this.openFileChooser(uploadMsg)
                }

                @Suppress("unused")
                fun openFileChooser(uploadMsg: ValueCallback<Uri?>, AcceptType: String?) {
                    this.openFileChooser(uploadMsg)
                }

                fun openFileChooser(uploadMsg: ValueCallback<Uri?>) {
//                    mUploadMessage = uploadMsg
//                    pickFile()
                }

                override fun onShowFileChooser(
                    bridgeWebView: WebView,
                    filePathCallback: ValueCallback<Array<Uri>>,
                    fileChooserParams: FileChooserParams
                ): Boolean {
//                    mUploadMessageArray = filePathCallback
//                    pickFile()
                    return true
                }
            })

//            bridgeWebView.addJavascriptInterface(
//                MainJavascriptInterface(bridgeWebView.getCallbacks(), bridgeWebView),
//                "WebViewJavascriptBridge"
//            )
            bridgeWebView.loadUrl("file:///android_asset/demo.html")
            val user = User("user", "password", "123456", "123456@163.com", Gender.MALE)

            bridgeWebView.callHandler(
                "functionInJs",
                Gson().toJson(user),
                object : CallBackFunction {
                    override fun onCallBack(data: String?) {
                        Log.d(TAG, "onCallBack: $data")
                    }

                })
            bridgeWebView.setDefaultHandler{data,function->
                Log.d(TAG, "jsCallJava DefaultHandler: $data")
            }
            bridgeWebView.registerHandler(
                "submitFromWeb"
            ) { data, function -> Log.d(TAG, "jsCallJava submitFromWeb: $data") }


        }
    }


}