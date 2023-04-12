package com.example.microdustapp.data.services.services

import com.example.microdustapp.BuildConfig
import com.example.microdustapp.data.services.models.tmcoordinates.TmCoordinatesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KakaoLocalApiService {

    @Headers("Authorization: KakaoAK ${BuildConfig.KAKAO_API_KEY}")
    @GET("/v2/local/geo/transcoord.json?output_coord=TM")
    suspend fun getTmCoordinates(
        @Query("x") longitude : Double,
        @Query("y") latitude : Double
    ) : Response<TmCoordinatesResponse>
 // 6ac7c57a7d6c9f50c6e5c6d3fc31c67b

 }