package com.example.airbnb

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/1c8ddca3-71e8-494d-a12c-30db7e101405")
    fun getHouseLiset() : Call<HouseDto>
}
