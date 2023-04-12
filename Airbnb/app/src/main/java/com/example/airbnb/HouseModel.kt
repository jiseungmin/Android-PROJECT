package com.example.airbnb

import com.naver.maps.geometry.LatLng

data class HouseModel(
    val id : String,
    val title : String,
    val price : String,
    val imgUrl : String,
    val lat : Double,
    val lng: Double
)
