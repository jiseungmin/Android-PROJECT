package com.example.bookreview.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// api에서 가져오는 json 데이터들을 가져옴

@Parcelize // 데이터 직렬화 하는 기능 -> 상세보기 화면으로 한번에 데이터 클래스를 intent 하기 위함
data class Book(
    @SerializedName("isbn") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("price") val priceSales: String?,
    @SerializedName("image") val coverSmallUrl: String,
    @SerializedName("link") val mobileLink: String
):Parcelable
