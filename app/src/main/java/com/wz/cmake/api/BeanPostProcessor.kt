package com.wz.cmake.api

interface BeanPostProcessor {
    fun postProcessBeforeInitialization(bean: Any, beanName: String): Any?
}