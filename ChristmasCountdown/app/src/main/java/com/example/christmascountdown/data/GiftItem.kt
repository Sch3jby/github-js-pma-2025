package com.example.christmascountdown.data

data class GiftItem(
    val id: String = System.currentTimeMillis().toString(),
    val recipientName: String,
    val giftName: String,
    val isPurchased: Boolean = false,
    val isWrapped: Boolean = false,
    val notes: String = ""
)