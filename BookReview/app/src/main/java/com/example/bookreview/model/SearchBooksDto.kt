package com.example.bookreview.model

import com.google.gson.annotations.SerializedName

data class SearchBooksDto( // Book 데이터 클래스를 맵핑하기 위해 전체 모델를 만들어 주는 Dto 생성
    @SerializedName("items") val books: List<Book>
)

