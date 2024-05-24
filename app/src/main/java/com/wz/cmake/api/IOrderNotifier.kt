package com.wz.cmake.api

import com.wz.cmake.model.Order

interface IOrderNotifier {
    fun notifyOrderShipped(order: Order)
}