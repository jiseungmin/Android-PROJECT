package com.example.microdustapp.data.services.airquality


import com.google.gson.annotations.SerializedName

data class AirqualityResponse(
    @SerializedName("response")
    val response: Response?
)