package com.wz.cmake

import android.util.Log
import android.view.View
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

interface OnClickListenerProxy {
    fun onClick(v: View)
}

class ClickListenerInvocationHandler(private val listener: OnClickListenerProxy) :
    InvocationHandler {
        val TAG = "ClickListenerProxy"
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        if (method?.name == "onClick" && args?.size == 1 && args[0] is View) {
            Log.e(TAG, "before onClick")
            listener.onClick(args[0] as View)
            Log.e(TAG, "after onClick")
        }
        return null
    }

}

fun View.setOnClickListenerProxy(listener: OnClickListenerProxy) {
    val proxy = Proxy.newProxyInstance(
        OnClickListenerProxy::class.java.classLoader,
        arrayOf(OnClickListenerProxy::class.java),
        ClickListenerInvocationHandler(listener)
    ) as OnClickListenerProxy
    this.setOnClickListener { v ->
        proxy.onClick(v)
    }
}

/**
 * 当需要对多个类或对象进行相似的日志记录时，动态代理可以派上用场。下面是一个简单的示例，演示了如何使用动态代理来实现日志记录功能：
 */
interface Loggable{
    fun logBefore(methodName:String)
    fun logAfter(methodName:String)
}

class LogInvocationHandler(private val target:Any):InvocationHandler{
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        val methodName = method?.name ?: ""
        (target as? Loggable)?.logBefore(methodName)
        val result = method?.invoke(target, *(args ?: arrayOf()))
        (target as? Loggable)?.logAfter(methodName)
        return result
    }

}

fun createLoggerProxy(target:Any):Any{
    return Proxy.newProxyInstance(target.javaClass.classLoader,target.javaClass.interfaces,LogInvocationHandler(target))
}

interface PerformanceMonitor{
    fun startMonitor()
    fun stopMonitor()
    fun logPerformance(methodName:String,executionTime:Long)
}

class PerformanceMonitorHandler(private val target: Any,private val monitor:PerformanceMonitor):InvocationHandler{
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        val methodName = method?.name ?: ""
        monitor.startMonitor()
        val startTime = System.currentTimeMillis()
        val result = method?.invoke(target, *(args ?: arrayOf()))
        val endTime = System.currentTimeMillis()
        monitor.stopMonitor()
        monitor.logPerformance(methodName, endTime - startTime)
        return result
    }

}

fun createPerformanceMonitorProxy(target:Any,monitor:PerformanceMonitor):Any{
    return Proxy.newProxyInstance(target.javaClass.classLoader,target.javaClass.interfaces,PerformanceMonitorHandler(target,monitor))
}

class RunnableMonitorHandler(private val target:Runnable,private val monitor:PerformanceMonitor):InvocationHandler{
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        val methodName = method?.name ?: ""
        monitor.startMonitor()
        val startTime = System.currentTimeMillis()
        val result = method?.invoke(target, *(args ?: arrayOf()))
        val endTime = System.currentTimeMillis()
        monitor.stopMonitor()
        monitor.logPerformance(methodName, endTime - startTime)
        return result
    }

}

fun createRunnableMonitorProxy(target:Runnable,monitor:PerformanceMonitor):Any{
    return Proxy.newProxyInstance(target.javaClass.classLoader,target.javaClass.interfaces,RunnableMonitorHandler(target,monitor))
}