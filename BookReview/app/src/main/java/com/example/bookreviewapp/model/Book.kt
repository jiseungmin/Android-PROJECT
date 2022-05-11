package com.example.bookreviewapp.model

import android.os.Parcelable
import androidx.versionedparcelable.VersionedParcelize
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize // 직렬화
data class Book(
   @SerializedName("isbn") val id: String,
   @SerializedName("title") val title: String,
   @SerializedName("description") val description: String,
   @SerializedName("price") val priceSales: String,
   @SerializedName("image") val coverSmallUrl: String,
   @SerializedName("link") val mobileLink: String
): Parcelable
