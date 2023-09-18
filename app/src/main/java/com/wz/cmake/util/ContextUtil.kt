package com.wz.cmake.util

import android.app.Application
import java.lang.reflect.Method

object ContextUtil {
    private val sApplication: Application? = null
    fun get(): Application? {
        return if (sApplication != null) sApplication else application
    }

    private val application: Application?
        private get() {
            var application: Application? = null
            var method: Method
            try {
                method = Class.forName("android.app.AppGlobals")
                    .getDeclaredMethod("getInitialApplication")
                method.setAccessible(true)
                application = method.invoke(null) as Application
            } catch (e: Exception) {
                try {
                    method = Class.forName("android.app.ActivityThread")
                        .getDeclaredMethod("currentApplication")
                    method.setAccessible(true)
                    application = method.invoke(null) as Application
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            return application
        }
}