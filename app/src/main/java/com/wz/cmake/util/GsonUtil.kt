package com.wz.cmake.util

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.LongSerializationPolicy
import com.wz.cmake.model.Gender
import com.wz.cmake.model.User

object GsonUtil {
    val TAG = GsonUtil::class.java.simpleName
    private val mGson = GsonBuilder()
        .setLongSerializationPolicy(LongSerializationPolicy.STRING)
        .serializeNulls()
        .setPrettyPrinting()
        .create()
    fun serialization(){
        Log.d(TAG, "gson.toJson(1):${mGson.toJson(1)}")
        Log.d(TAG, "gson.toJson(\"abcd\"):${mGson.toJson("abcd")}")
        val values = intArrayOf(1, 3, 5)
        Log.d(TAG, "gson.toJson(intArray):${mGson.toJson(values)}")
    }

    fun deserialization(){
        val i = mGson.fromJson("1", Int::class.java)
        Log.d(TAG, "i:${i}")
        val l = mGson.fromJson("1", Long::class.java)
        Log.d(TAG, "l:${l}")
        val bool = mGson.fromJson("false", Boolean::class.java)
        Log.d(TAG, "bool:${bool}")
        val str = mGson.fromJson("\"abc\"", String::class.java)
        Log.d(TAG, "str:${str}")
        val strArray = mGson.fromJson("[\"abc\"]", Array<String>::class.java)
        Log.d(TAG, "strArray:${strArray}")
    }
    fun user2Json(){
        val wz = User("wz","1011","17621536845","wangzhen11210107@gmail.com",Gender.MALE)
        val wzJson = mGson.toJson(wz)
        Log.d(TAG, "wzJson:${wzJson}")
    }

    fun json2User(){
        val json = "{\n" +
                "    \"email\": \"wangzhen11210107@gmail.com\",\n" +
                "    \"gender\": 1,\n" +
                "    \"password\": \"1011\",\n" +
                "    \"phoneNumber\": \"17621536845\",\n" +
                "    \"name\": \"wz\"\n" +
                "}"
        val wz = mGson.fromJson(json, User::class.java)
        Log.d(TAG, "wzUser:${wz}")
    }
}