package com.example.microdustapp.data.services.airquality


import com.google.gson.annotations.SerializedName

data class Body(
    @SerializedName("items")
    val measuredValues: List<measuredValue?>?,
    @SerializedName("numOfRows")
    val numOfRows: Int?,
    @SerializedName("pageNo")
    val pageNo: Int?,
    @SerializedName("totalCount")
    val totalCount: Int?
)