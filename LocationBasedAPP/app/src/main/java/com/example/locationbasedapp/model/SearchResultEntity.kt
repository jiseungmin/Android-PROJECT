package com.example.locationbasedapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SearchResultEntity(
    val fulladress : String,
    val name: String,
    val locationLatng : LocationLatLngEntity
) : Parcelable
