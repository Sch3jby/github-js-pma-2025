package com.example.semestralproject

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://sportovni-denik-default-rtdb.europe-west1.firebasedatabase.app")
    private val workoutsRef: DatabaseReference = database.getReference("workouts")

    // Získání všech tréninků jako Flow
    fun getWorkoutsFlow(): Flow<List<Workout>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val workouts = mutableListOf<Workout>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(WorkoutFirebase::class.java)?.let { firebaseWorkout ->
                        val workout = firebaseWorkout.toWorkout()
                        workout.firebaseKey = childSnapshot.key ?: ""
                        workouts.add(workout)
                    }
                }
                trySend(workouts)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        workoutsRef.addValueEventListener(listener)

        awaitClose {
            workoutsRef.removeEventListener(listener)
        }
    }

    // Přidání nového tréninku
    suspend fun addWorkout(workout: Workout): Result<String> {
        return try {
            val key = workoutsRef.push().key ?: return Result.failure(Exception("Nepodařilo se vygenerovat klíč"))
            val firebaseWorkout = WorkoutFirebase.fromWorkout(workout.copy(id = key.hashCode()))
            workoutsRef.child(key).setValue(firebaseWorkout).await()
            Result.success(key)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Aktualizace tréninku
    suspend fun updateWorkout(workoutId: String, workout: Workout): Result<Unit> {
        return try {
            val firebaseWorkout = WorkoutFirebase.fromWorkout(workout)
            workoutsRef.child(workoutId).setValue(firebaseWorkout).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Smazání tréninku
    suspend fun deleteWorkout(workoutId: String): Result<Unit> {
        return try {
            workoutsRef.child(workoutId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Aktualizace stavu dokončení
    suspend fun updateWorkoutCompletion(workoutId: String, isCompleted: Boolean): Result<Unit> {
        return try {
            workoutsRef.child(workoutId).child("completed").setValue(isCompleted).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Model pro Firebase (bez Uri, který není serializovatelný)
data class WorkoutFirebase(
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var intensity: String = "",
    var duration: Int = 0,
    var date: String = "",
    var completed: Boolean = false,
    var imageUri: String = ""
) {
    fun toWorkout(): Workout {
        return Workout(
            id = id,
            name = name,
            description = description,
            intensity = intensity,
            duration = duration,
            date = date,
            isCompleted = completed,
            imageUri = imageUri.takeIf { it.isNotEmpty() },
            firebaseKey = ""
        )
    }

    companion object {
        fun fromWorkout(workout: Workout): WorkoutFirebase {
            return WorkoutFirebase(
                id = workout.id,
                name = workout.name,
                description = workout.description,
                intensity = workout.intensity,
                duration = workout.duration,
                date = workout.date,
                completed = workout.isCompleted,
                imageUri = workout.imageUri ?: ""
            )
        }
    }
}