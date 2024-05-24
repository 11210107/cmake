package com.wz.cmake.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Since
import com.google.gson.annotations.Until
import com.wz.cmake.adapter.GenderAdapter

data class User(
    @SerializedName("name")
    val username: String,
    @Expose
    val password: String,
    @Since(1.1)
    val phoneNumber: String,
    @Until(1.2)
    val email: String,
    @JsonAdapter(GenderAdapter::class)
    val gender: Gender
)


enum class Gender {
    MALE,
    FEMALE,
    UNKNOWN
}
