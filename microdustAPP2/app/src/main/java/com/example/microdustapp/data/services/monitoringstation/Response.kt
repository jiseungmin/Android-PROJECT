package com.example.microdustapp.data.services.monitoringstation


import com.google.gson.annotations.SerializedName

data class Response<T>(
    @SerializedName("body")
    val body: Body?,
    @SerializedName("header")
    val header: Header?
)