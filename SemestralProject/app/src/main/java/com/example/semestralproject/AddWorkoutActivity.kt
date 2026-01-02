package com.example.semestralproject

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.semestralproject.databinding.ActivityAddWorkoutBinding
import java.text.SimpleDateFormat
import java.util.*

class AddWorkoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddWorkoutBinding
    private var selectedImageUri: Uri? = null
    private var selectedDate: String = ""
    private var selectedWorkoutType: WorkoutType = WorkoutType.OTHER

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            binding.ivWorkoutImage.setImageURI(it)
            binding.ivWorkoutImage.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWorkoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Nový trénink"

        setupWorkoutTypeSpinner()
        setupDatePicker()
        setupListeners()
    }

    private fun setupWorkoutTypeSpinner() {
        val workoutTypes = WorkoutType.values().map { it.displayName }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workoutTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkoutType.adapter = adapter

        binding.spinnerWorkoutType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedWorkoutType = WorkoutType.values()[position]
                updateFieldsVisibility()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedWorkoutType = WorkoutType.OTHER
            }
        }
    }

    private fun updateFieldsVisibility() {
        val fields = selectedWorkoutType.fields

        // Vzdálenost zobrazit jen pokud je v seznamu polí
        if (fields.contains(WorkoutField.DISTANCE)) {
            binding.layoutDistance.visibility = View.VISIBLE
        } else {
            binding.layoutDistance.visibility = View.GONE
            binding.etDistance.setText("")
        }
    }

    private fun setupDatePicker() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)
        binding.tvSelectedDate.text = "Datum: $selectedDate"

        binding.btnSelectDate.setOnClickListener {
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    selectedDate = dateFormat.format(calendar.time)
                    binding.tvSelectedDate.text = "Datum: $selectedDate"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupListeners() {
        binding.btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        binding.btnSaveWorkout.setOnClickListener {
            saveWorkout()
        }
    }

    private fun saveWorkout() {
        val name = binding.etWorkoutName.text.toString().trim()
        val description = binding.etWorkoutDescription.text.toString().trim()
        val durationStr = binding.etDuration.text.toString().trim()
        val distanceStr = binding.etDistance.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Zadejte název tréninku", Toast.LENGTH_SHORT).show()
            return
        }

        if (durationStr.isEmpty()) {
            Toast.makeText(this, "Zadejte délku trvání", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = durationStr.toIntOrNull() ?: 0
        if (duration <= 0) {
            Toast.makeText(this, "Zadejte platnou délku trvání", Toast.LENGTH_SHORT).show()
            return
        }

        // Kontrola vzdálenosti, pokud je pole viditelné
        var distance: Float? = null
        if (binding.layoutDistance.visibility == View.VISIBLE && distanceStr.isNotEmpty()) {
            distance = distanceStr.toFloatOrNull()
            if (distance == null || distance <= 0) {
                Toast.makeText(this, "Zadejte platnou vzdálenost", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val intensity = when (binding.radioGroupIntensity.checkedRadioButtonId) {
            R.id.radioLow -> "Nízká"
            R.id.radioMedium -> "Střední"
            R.id.radioHigh -> "Vysoká"
            else -> "Střední"
        }

        val workout = Workout(
            id = System.currentTimeMillis().toInt(),
            name = name,
            description = description,
            workoutType = selectedWorkoutType,
            intensity = intensity,
            duration = duration,
            distance = distance,
            date = selectedDate,
            isCompleted = false,
            imageUri = selectedImageUri?.toString()
        )

        val resultIntent = Intent()
        resultIntent.putExtra("NEW_WORKOUT", workout)
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}