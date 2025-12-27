package com.example.christmascountdown.data

data class WishlistItem(
    val id: String = System.currentTimeMillis().toString(),
    val name: String,
    val category: String, // "Toys", "Books", "Electronics", "Other"
    val priority: Int = 1, // 1-5
    val notes: String = ""
)