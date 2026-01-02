package com.example.semestralproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.semestralproject.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val workoutList = mutableListOf<Workout>()
    private lateinit var adapter: WorkoutAdapter
    private lateinit var firebaseRepository: FirebaseRepository
    private var lastDeletedWorkout: Workout? = null
    private var lastDeletedPosition: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Moje tréninky"

        firebaseRepository = FirebaseRepository()

        setupAdapter()
        setupListeners()
        observeWorkouts()
    }

    private fun setupAdapter() {
        adapter = WorkoutAdapter(
            workoutList,
            onItemClick = { workout ->
                val intent = Intent(this, WorkoutDetailActivity::class.java)
                intent.putExtra("WORKOUT", workout)
                startActivityForResult(intent, REQUEST_CODE_DETAIL)
            },
            onDeleteClick = { workout, position ->
                deleteWorkout(workout, position)
            },
            onCheckboxChange = { workout, isChecked ->
                workout.isCompleted = isChecked
                updateWorkoutCompletion(workout)
            }
        )
        binding.listViewWorkouts.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnAddWorkout.setOnClickListener {
            val intent = Intent(this, AddWorkoutActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD)
        }
    }

    private fun observeWorkouts() {
        lifecycleScope.launch {
            firebaseRepository.getWorkoutsFlow().collect { workouts ->
                workoutList.clear()
                workoutList.addAll(workouts)
                adapter.notifyDataSetChanged()
                updateEmptyState()
            }
        }
    }

    private fun updateWorkoutCompletion(workout: Workout) {
        lifecycleScope.launch {
            if (workout.firebaseKey.isNotEmpty()) {
                firebaseRepository.updateWorkoutCompletion(workout.firebaseKey, workout.isCompleted)
                    .onSuccess {
                        Toast.makeText(
                            this@MainActivity,
                            "Trénink ${if (workout.isCompleted) "dokončen" else "nedokončen"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .onFailure { e ->
                        Toast.makeText(
                            this@MainActivity,
                            "Chyba při aktualizaci: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun deleteWorkout(workout: Workout, position: Int) {
        lastDeletedWorkout = workout
        lastDeletedPosition = position

        lifecycleScope.launch {
            if (workout.firebaseKey.isNotEmpty()) {
                firebaseRepository.deleteWorkout(workout.firebaseKey)
                    .onSuccess {
                        Snackbar.make(binding.root, "Trénink smazán", Snackbar.LENGTH_LONG)
                            .setAction("Vrátit") {
                                lastDeletedWorkout?.let {
                                    lifecycleScope.launch {
                                        firebaseRepository.addWorkout(it)
                                    }
                                }
                            }
                            .show()
                    }
                    .onFailure { e ->
                        Toast.makeText(
                            this@MainActivity,
                            "Chyba při mazání: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_ADD -> {
                    data?.getSerializableExtra("NEW_WORKOUT")?.let { workout ->
                        val newWorkout = workout as Workout
                        lifecycleScope.launch {
                            firebaseRepository.addWorkout(newWorkout)
                                .onSuccess {
                                    val customToast = layoutInflater.inflate(R.layout.custom_toast, null)
                                    Toast(this@MainActivity).apply {
                                        duration = Toast.LENGTH_SHORT
                                        view = customToast
                                        show()
                                    }
                                }
                                .onFailure { e ->
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Chyba při přidávání: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    }
                }
                REQUEST_CODE_DETAIL -> {
                    data?.getSerializableExtra("UPDATED_WORKOUT")?.let { updated ->
                        val updatedWorkout = updated as Workout
                        if (updatedWorkout.firebaseKey.isNotEmpty()) {
                            lifecycleScope.launch {
                                firebaseRepository.updateWorkout(updatedWorkout.firebaseKey, updatedWorkout)
                                    .onFailure { e ->
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Chyba při aktualizaci: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SettingsFragment())
                    .addToBackStack(null)
                    .commit()
                binding.fragmentContainer.visibility = android.view.View.VISIBLE
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateEmptyState() {
        if (workoutList.isEmpty()) {
            binding.tvEmptyState.visibility = android.view.View.VISIBLE
            binding.listViewWorkouts.visibility = android.view.View.GONE
        } else {
            binding.tvEmptyState.visibility = android.view.View.GONE
            binding.listViewWorkouts.visibility = android.view.View.VISIBLE
        }
    }

    companion object {
        const val REQUEST_CODE_ADD = 1
        const val REQUEST_CODE_DETAIL = 2
    }
}