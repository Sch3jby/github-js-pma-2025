package com.example.codequizmaster.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entita reprezentující otázku v kvízu
 */
@Entity(tableName = "questions")
data class Question(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val questionText: String,
    val correctAnswer: String,
    val wrongAnswer1: String,
    val wrongAnswer2: String,
    val wrongAnswer3: String,
    val difficulty: String, // "EASY", "MEDIUM", "HARD"
    val category: String
)