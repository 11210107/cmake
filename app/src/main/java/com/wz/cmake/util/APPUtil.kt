package com.wz.cmake.util

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.util.DisplayMetrics
import android.view.WindowManager


class APPUtil {
    companion object{
        @JvmStatic
        fun getDisplayWidth(context: Context): Int {
            val dm = DisplayMetrics()
            val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(dm)
            return dm.widthPixels // 宽
        }
    }
}