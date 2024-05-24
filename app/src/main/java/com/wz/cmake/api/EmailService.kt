package com.wz.cmake.api

import android.util.Log
import com.wz.cmake.model.Order

class EmailService:IOrderNotifier {

    override fun notifyOrderShipped(order: Order) {
        val subject = "Order ${order.id} is shipped"
        val content = "Your order has been shipped."
        send(subject,order.customerEmail,content)
    }

    private fun send(subject: String, customerEmail: String, content: String) {
        Log.d("IOrderNotifier", "EmailService send order shipped message")
    }
}

class SMSService:IOrderNotifier {
    override fun notifyOrderShipped(order: Order) {
        val subject = "Order ${order.id} is shipped"
        val content = "Your order has been shipped."
        send(subject,order.phoneNum,content)
    }

    private fun send(subject: String, phoneNum: String, content: String) {
        Log.d("IOrderNotifier", "SMSService send order shipped message")
    }

}