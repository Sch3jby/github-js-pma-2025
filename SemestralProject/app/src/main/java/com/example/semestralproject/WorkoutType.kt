package com.example.semestralproject

enum class WorkoutType(val displayName: String, val fields: List<WorkoutField>) {
    RUNNING("Běh", listOf(WorkoutField.DURATION, WorkoutField.DISTANCE)),
    CYCLING("Kolo", listOf(WorkoutField.DURATION, WorkoutField.DISTANCE)),
    SWIMMING("Plavání", listOf(WorkoutField.DURATION, WorkoutField.DISTANCE)),
    GYM("Posilování", listOf(WorkoutField.DURATION)),
    YOGA("Jóga", listOf(WorkoutField.DURATION)),
    WALKING("Procházka", listOf(WorkoutField.DURATION, WorkoutField.DISTANCE)),
    OTHER("Jiné", listOf(WorkoutField.DURATION));

    companion object {
        fun fromString(value: String): WorkoutType {
            return values().find { it.name == value } ?: OTHER
        }
    }
}

enum class WorkoutField {
    DURATION,    // Čas/Délka trvání
    DISTANCE     // Vzdálenost
}