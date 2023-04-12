package com.example.tradeapp.chat

data class ChatList(
    val buyerId: String,
    val sellerId: String,
    val itemTitle: String,
    val key: Long
) {
    constructor() : this("", "", "", 0)
}