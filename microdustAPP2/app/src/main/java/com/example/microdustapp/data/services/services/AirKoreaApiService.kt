package com.example.microdustapp.data.services.services

import com.example.microdustapp.BuildConfig
import com.example.microdustapp.data.services.airquality.AirqualityResponse
import com.example.microdustapp.data.services.monitoringstation.MonitoringStationsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AirKoreaApiService {

    @GET("B552584/MsrstnInfoInqireSvc/getNearbyMsrstnList"+"?serviceKey=${BuildConfig.AIRKOREA_SERVICE_KEY}"+"&returnType=json")
    suspend fun getNearbyMonitoringStation(
        @Query("tmX") tmX: Double,
        @Query("tmY") tmY: Double
    ): Response<MonitoringStationsResponse>

    @GET("B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty"+"?serviceKey=${BuildConfig.AIRKOREA_SERVICE_KEY}"+"&returnType=json"+"&dataTerm=DAILY"+"&ver=1.3")
    suspend fun getRealtimeAirQualties(
        @Query("stationName") stationName:String
    ):Response<AirqualityResponse>
}