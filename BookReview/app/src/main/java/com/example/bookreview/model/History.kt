package com.example.bookreview.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity // 데이터베이스 내에 존재하는 테이블 가리킵니다.
data class History (
    @PrimaryKey val uid: Int?,
    @ColumnInfo (name = "keyword") val keyword: String?
)