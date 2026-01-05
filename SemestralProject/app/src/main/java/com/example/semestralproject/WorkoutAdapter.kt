package com.example.semestralproject

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip

class WorkoutAdapter(
    private val workouts: List<Workout>,
    private val onItemClick: (Workout) -> Unit,
    private val onDeleteClick: (Workout, Int) -> Unit,
    private val onCheckboxChange: (Workout, Boolean) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = workouts.size

    override fun getItem(position: Int): Any = workouts[position]

    override fun getItemId(position: Int): Long = workouts[position].id.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.item_workout, parent, false)

        val workout = workouts[position]

        // Find views
        val cardView = view as MaterialCardView
        val tvWorkoutIcon = view.findViewById<TextView>(R.id.tvWorkoutIcon)
        val tvName = view.findViewById<TextView>(R.id.tvWorkoutName)
        val tvDate = view.findViewById<TextView>(R.id.tvWorkoutDate)
        val chipDuration = view.findViewById<Chip>(R.id.chipDuration)
        val chipIntensity = view.findViewById<Chip>(R.id.chipIntensity)
        val cbCompleted = view.findViewById<CheckBox>(R.id.cbWorkoutCompleted)
        val btnDelete = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnDeleteWorkout)
        val cardImageThumb = view.findViewById<MaterialCardView>(R.id.cardImageThumb)
        val ivImage = view.findViewById<ImageView>(R.id.ivWorkoutThumb)

        // Set workout icon based on type
        tvWorkoutIcon.text = when (workout.workoutType) {
            WorkoutType.RUNNING -> "🏃"
            WorkoutType.CYCLING -> "🚴"
            WorkoutType.SWIMMING -> "🏊"
            WorkoutType.GYM -> "💪"
            WorkoutType.YOGA -> "🧘"
            WorkoutType.WALKING -> "🚶"
            else -> "⚡"
        }

        // Set data
        tvName.text = workout.name
        tvDate.text = workout.date

        // Duration chip
        chipDuration.text = workout.getAdditionalInfo()

        // Intensity chip with color
        chipIntensity.text = workout.intensity
        when (workout.intensity) {
            "Nízká" -> {
                chipIntensity.setChipBackgroundColorResource(R.color.intensity_low)
                chipIntensity.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
            }
            "Střední" -> {
                chipIntensity.setChipBackgroundColorResource(R.color.intensity_medium)
                chipIntensity.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
            }
            "Vysoká" -> {
                chipIntensity.setChipBackgroundColorResource(R.color.intensity_high)
                chipIntensity.setTextColor(ContextCompat.getColor(view.context, android.R.color.white))
            }
        }

        // Checkbox
        cbCompleted.isChecked = workout.isCompleted
        cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            onCheckboxChange(workout, isChecked)
        }

        // Image
        workout.imageUri?.let {
            ivImage.setImageURI(Uri.parse(it))
            cardImageThumb.visibility = View.VISIBLE
        } ?: run {
            cardImageThumb.visibility = View.GONE
        }

        // Click listeners
        cardView.setOnClickListener {
            onItemClick(workout)
        }

        btnDelete.setOnClickListener {
            onDeleteClick(workout, position)
        }

        return view
    }
}