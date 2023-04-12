package com.example.tradeapp.chatdetail

data class ChatItem(
    val sendID: String,
    val message: String
) {
    constructor() : this("", "")
}
