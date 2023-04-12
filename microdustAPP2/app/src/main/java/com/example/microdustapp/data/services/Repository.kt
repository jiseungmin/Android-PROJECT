package com.example.microdustapp.data.services

import com.example.microdustapp.BuildConfig
import com.example.microdustapp.data.services.airquality.measuredValue
import com.example.microdustapp.data.services.monitoringstation.MonitoringStation
import com.example.microdustapp.data.services.services.AirKoreaApiService
import com.example.microdustapp.data.services.services.KakaoLocalApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

// 레트로핏 생성
object Repository {

    suspend fun getNearbyMonitoringStation(latitude:Double, longitude:Double):MonitoringStation?{
        val tmCoordinates = kakaoLocalApiService
            .getTmCoordinates(longitude,latitude)
            .body()
            ?.documents
            ?.firstOrNull()

        val tmX = tmCoordinates?.x
        val tmY = tmCoordinates?.y

        return airKoreaApiService
            .getNearbyMonitoringStation(tmX!!,tmY!!)
            .body()
            ?.response
            ?.body
            ?.monitoringStations
            ?.minByOrNull { it?.tm ?: Double.MAX_VALUE}

    }

    suspend fun getLatestAirQualityData(stationname: String): measuredValue? =
        airKoreaApiService.getRealtimeAirQualties(stationname)
            .body()
            ?.response
            ?.body
            ?.measuredValues
            ?.firstOrNull()

    private val kakaoLocalApiService: KakaoLocalApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.KAKAO_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }

    private val airKoreaApiService: AirKoreaApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Url.AIR_KOREA_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(buildHttpClient())
            .build()
            .create()
    }


    private fun buildHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                }
            )
            .build()
}