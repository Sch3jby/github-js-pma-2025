package com.example.semestralproject

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.semestralproject.databinding.ActivityWorkoutDetailBinding
import com.google.android.material.snackbar.Snackbar

class WorkoutDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutDetailBinding
    private lateinit var workout: Workout
    private var selectedImageUri: Uri? = null
    private var selectedWorkoutType: WorkoutType = WorkoutType.OTHER

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivDetailImage.setImageURI(it)
            binding.ivDetailImage.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        workout = intent.getSerializableExtra("WORKOUT") as Workout

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail tréninku"

        setupWorkoutTypeSpinner()
        loadWorkoutData()
        setupListeners()
    }

    private fun setupWorkoutTypeSpinner() {
        val workoutTypes = WorkoutType.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workoutTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDetailWorkoutType.adapter = adapter

        binding.spinnerDetailWorkoutType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedWorkoutType = WorkoutType.values()[position]
                updateFieldsVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedWorkoutType = workout.workoutType
            }
        }
    }

    private fun updateFieldsVisibility() {
        val fields = selectedWorkoutType.fields

        if (fields.contains(WorkoutField.DISTANCE)) {
            binding.layoutDetailDistance.visibility = View.VISIBLE
        } else {
            binding.layoutDetailDistance.visibility = View.GONE
        }
    }

    private fun loadWorkoutData() {
        binding.etDetailName.setText(workout.name)
        binding.etDetailDescription.setText(workout.description)
        binding.etDetailDuration.setText(workout.duration.toString())

        // Nastavení typu aktivity
        val typePosition = WorkoutType.values().indexOf(workout.workoutType)
        binding.spinnerDetailWorkoutType.setSelection(typePosition)
        selectedWorkoutType = workout.workoutType

        // Nastavení vzdálenosti
        workout.distance?.let {
            binding.etDetailDistance.setText(it.toString())
        }

        binding.tvDetailDate.text = "Datum: ${workout.date}"
        binding.cbDetailCompleted.isChecked = workout.isCompleted

        when (workout.intensity) {
            "Nízká" -> binding.radioGroupDetailIntensity.check(R.id.radioDetailLow)
            "Střední" -> binding.radioGroupDetailIntensity.check(R.id.radioDetailMedium)
            "Vysoká" -> binding.radioGroupDetailIntensity.check(R.id.radioDetailHigh)
        }

        workout.imageUri?.let {
            binding.ivDetailImage.setImageURI(Uri.parse(it))
            binding.ivDetailImage.visibility = View.VISIBLE
            selectedImageUri = Uri.parse(it)
        }

        updateFieldsVisibility()
    }

    private fun setupListeners() {
        binding.btnChangeImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnUpdateWorkout.setOnClickListener {
            updateWorkout()
        }

        binding.btnDeleteWorkout.setOnClickListener {
            Snackbar.make(binding.root, "Opravdu smazat trénink?", Snackbar.LENGTH_LONG)
                .setAction("Ano") {
                    deleteWorkout()
                }
                .show()
        }
    }

    private fun updateWorkout() {
        val name = binding.etDetailName.text.toString().trim()
        val description = binding.etDetailDescription.text.toString().trim()
        val durationStr = binding.etDetailDuration.text.toString().trim()
        val distanceStr = binding.etDetailDistance.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Zadejte název tréninku", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = durationStr.toIntOrNull() ?: 0
        if (duration <= 0) {
            Toast.makeText(this, "Zadejte platnou délku trvání", Toast.LENGTH_SHORT).show()
            return
        }

        var distance: Float? = null
        if (binding.layoutDetailDistance.visibility == View.VISIBLE && distanceStr.isNotEmpty()) {
            distance = distanceStr.toFloatOrNull()
            if (distance == null || distance <= 0) {
                Toast.makeText(this, "Zadejte platnou vzdálenost", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val intensity = when (binding.radioGroupDetailIntensity.checkedRadioButtonId) {
            R.id.radioDetailLow -> "Nízká"
            R.id.radioDetailMedium -> "Střední"
            R.id.radioDetailHigh -> "Vysoká"
            else -> workout.intensity
        }

        workout.name = name
        workout.description = description
        workout.workoutType = selectedWorkoutType
        workout.duration = duration
        workout.distance = distance
        workout.intensity = intensity
        workout.isCompleted = binding.cbDetailCompleted.isChecked
        workout.imageUri = selectedImageUri?.toString()

        val resultIntent = Intent()
        resultIntent.putExtra("UPDATED_WORKOUT", workout)
        setResult(RESULT_OK, resultIntent)

        Toast.makeText(this, "Trénink aktualizován", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun deleteWorkout() {
        setResult(RESULT_CANCELED)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}