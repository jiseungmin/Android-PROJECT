package com.example.bookreviewapp.model

import com.google.gson.annotations.SerializedName

data class SearchBooksDto(
    @SerializedName("items") val books: List<Book>
)
