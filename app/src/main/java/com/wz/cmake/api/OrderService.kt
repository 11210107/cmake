package com.wz.cmake.api

import com.wz.cmake.model.Order

class OrderService(val orderNotifier: IOrderNotifier) {
    fun notifyOrderShipped(order: Order) {
        order.status = 1
        orderNotifier.notifyOrderShipped(order)
    }
}