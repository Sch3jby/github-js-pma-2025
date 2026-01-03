package com.example.semestralproject

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class WeightRecord(
    val weight: Float,
    val date: String,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable {

    companion object {
        fun create(weight: Float): WeightRecord {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            return WeightRecord(
                weight = weight,
                date = dateFormat.format(Date()),
                timestamp = System.currentTimeMillis()
            )
        }
    }
}