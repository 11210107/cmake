package com.wz.cmake.util

import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.wz.cmake.audio.AudioRecordManager
import java.io.File
import java.util.Locale
import kotlin.text.*

class FileUtil {


    companion object{
        val TAG = FileUtil::class.java.simpleName

        @JvmStatic
        fun getFilePath():String?{
            val recordDir = AudioRecordManager.getInstance().getCurrentConfig().getRecordDir()
            if (!createOrExistsDir(recordDir)) {
                Log.w(TAG, "文件夹创建失败：${recordDir}")
                return null
            }
            val fileName = "record_${DateUtil.millis2String(System.currentTimeMillis(),DateUtil.SIMPLE_DATE_FORMAT)}"
            return String.format(Locale.getDefault(), "%s%s%s",recordDir,fileName,AudioRecordManager.getInstance().getCurrentConfig().format.extension)
        }

        @JvmStatic
        fun getTempFilePath():String {
            val fileDir = String.format(
                Locale.getDefault(),
                "%s/Record/",
                ContextUtil.get()?.dataDir?.absoluteFile
//                Environment.getExternalStorageDirectory().absoluteFile
            )
            if (!createOrExistsDir(fileDir)) {
                Log.e(TAG, "Could not create directory:${fileDir}")
            }
            val fileName = String.format(
                Locale.getDefault(),
                "record_tmp_%s",
                DateUtil.millis2String(System.currentTimeMillis(), DateUtil.SIMPLE_DATE_FORMAT)
            )
            return String.format(Locale.getDefault(), "%s%s.pcm", fileDir, fileName)
        }

        @JvmStatic
        fun createOrExistsDir(dirPath:String):Boolean{
            return createOrExistsDir(getFileByPath(dirPath))
        }

        @JvmStatic
        fun createOrExistsDir(file: File?):Boolean{
            return file != null && (if (file.exists()) file.isDirectory else file.mkdirs())
        }

        @JvmStatic
        fun getFileByPath(path:String):File?{
            return if (TextUtils.isEmpty(path)) null else File(path)
        }

        @JvmStatic
        fun isFile(file:File?):Boolean{
            return file?.isFile?:false && file?.exists()?:false
        }

        @JvmStatic
        fun isFileEmpty(file:File?):Boolean{
            return (file?.length() ?: 0 > 0)
        }

    }
}