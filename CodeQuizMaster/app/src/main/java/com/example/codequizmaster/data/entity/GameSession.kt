package com.example.codequizmaster.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entita reprezentující jednu odehranou hru
 */
@Entity(
    tableName = "game_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class GameSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val score: Int,
    val correctAnswers: Int,
    val totalQuestions: Int,
    val difficulty: String,
    val playedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int
)