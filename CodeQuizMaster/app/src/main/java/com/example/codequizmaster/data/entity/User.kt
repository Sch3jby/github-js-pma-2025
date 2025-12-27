package com.example.codequizmaster.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entita reprezentující uživatele aplikace
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val totalGamesPlayed: Int = 0,
    val totalScore: Int = 0,
    val highestScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastPlayed: Long = System.currentTimeMillis()
)