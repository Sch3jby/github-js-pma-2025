package com.example.semestralproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.semestralproject.databinding.FragmentWorkoutsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class WorkoutsFragment : Fragment() {

    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private val workoutList = mutableListOf<Workout>()
    private lateinit var adapter: WorkoutAdapter
    private lateinit var firebaseRepository: FirebaseRepository
    private var lastDeletedWorkout: Workout? = null
    private var lastDeletedPosition: Int = -1

    private val addWorkoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.getSerializableExtra("NEW_WORKOUT")?.let { workout ->
                val newWorkout = workout as Workout
                lifecycleScope.launch {
                    firebaseRepository.addWorkout(newWorkout)
                        .onSuccess {
                            val customToast = layoutInflater.inflate(R.layout.custom_toast, null)
                            Toast(requireContext()).apply {
                                duration = Toast.LENGTH_SHORT
                                view = customToast
                                show()
                            }
                        }
                        .onFailure { e ->
                            Toast.makeText(
                                requireContext(),
                                "Chyba při přidávání: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }
        }
    }

    private val detailWorkoutLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.getSerializableExtra("UPDATED_WORKOUT")?.let { updated ->
                val updatedWorkout = updated as Workout
                if (updatedWorkout.firebaseKey.isNotEmpty()) {
                    lifecycleScope.launch {
                        firebaseRepository.updateWorkout(updatedWorkout.firebaseKey, updatedWorkout)
                            .onFailure { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Chyba při aktualizaci: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseRepository = FirebaseRepository()

        setupAdapter()
        setupListeners()
        observeWorkouts()
    }

    private fun setupAdapter() {
        adapter = WorkoutAdapter(
            workoutList,
            onItemClick = { workout ->
                val intent = Intent(requireContext(), WorkoutDetailActivity::class.java)
                intent.putExtra("WORKOUT", workout)
                detailWorkoutLauncher.launch(intent)
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
            val intent = Intent(requireContext(), AddWorkoutActivity::class.java)
            addWorkoutLauncher.launch(intent)
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
                            requireContext(),
                            "Trénink ${if (workout.isCompleted) "dokončen" else "nedokončen"}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .onFailure { e ->
                        Toast.makeText(
                            requireContext(),
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
                            requireContext(),
                            "Chyba při mazání: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun updateEmptyState() {
        if (workoutList.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.listViewWorkouts.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.listViewWorkouts.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}