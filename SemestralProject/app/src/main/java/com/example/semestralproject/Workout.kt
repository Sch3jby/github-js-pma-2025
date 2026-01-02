package com.example.semestralproject

import java.io.Serializable

data class Workout(
    val id: Int,
    var name: String,
    var description: String,
    var workoutType: WorkoutType = WorkoutType.OTHER,  // Typ aktivity
    var intensity: String,
    var duration: Int,  // Čas v minutách
    var distance: Float? = null,  // Vzdálenost v km (volitelné)
    var date: String,
    var isCompleted: Boolean = false,
    var imageUri: String? = null,
    var firebaseKey: String = ""
) : Serializable {

    // Pomocná metoda pro zobrazení dodatečných informací
    fun getAdditionalInfo(): String {
        return if (distance != null && distance!! > 0) {
            "$duration min • ${String.format("%.2f", distance)} km"
        } else {
            "$duration min"
        }
    }
}