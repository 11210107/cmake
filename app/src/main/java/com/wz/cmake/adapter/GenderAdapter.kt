package com.wz.cmake.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.wz.cmake.model.Gender

class GenderAdapter: TypeAdapter<Gender>() {
    override fun write(out: JsonWriter, value: Gender?) {
        when (value) {
            Gender.UNKNOWN -> out.nullValue()
            Gender.MALE -> out.value("1")
            Gender.FEMALE -> out.value("2")
            else -> {}
        }
    }

    override fun read(`in`: JsonReader?): Gender {
        return when(`in`?.peek()){
            JsonToken.NULL ->{
                `in`.nextNull()
                Gender.UNKNOWN
            }
            JsonToken.NUMBER ->{
                when (`in`.nextInt()) {
                    1 -> Gender.MALE
                    2 -> Gender.FEMALE
                    else -> Gender.UNKNOWN
                }
            }
            else -> Gender.UNKNOWN
        }
    }
}