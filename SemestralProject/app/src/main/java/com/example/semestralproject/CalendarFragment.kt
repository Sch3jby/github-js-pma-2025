package com.example.semestralproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.semestralproject.databinding.FragmentCalendarBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseRepository: FirebaseRepository
    private var allWorkouts = listOf<Workout>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseRepository = FirebaseRepository()

        setupCalendar()
        observeWorkouts()
    }

    private fun setupCalendar() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = String.format("%02d.%02d.%04d", dayOfMonth, month + 1, year)
            showWorkoutsForDate(selectedDate)
        }
    }

    private fun observeWorkouts() {
        lifecycleScope.launch {
            firebaseRepository.getWorkoutsFlow().collect { workouts ->
                allWorkouts = workouts

                // Zobrazíme info o dnešním dni při prvním načtení
                val today = Calendar.getInstance()
                val todayString = String.format(
                    "%02d.%02d.%04d",
                    today.get(Calendar.DAY_OF_MONTH),
                    today.get(Calendar.MONTH) + 1,
                    today.get(Calendar.YEAR)
                )
                showWorkoutsForDate(todayString)

                // Zobrazíme celkový počet tréninků
                updateSummary(workouts)
            }
        }
    }

    private fun updateSummary(workouts: List<Workout>) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val today = Calendar.getInstance()
        val todayString = dateFormat.format(today.time)

        val pastWorkouts = workouts.filter { workout ->
            try {
                val workoutDate = dateFormat.parse(workout.date)
                workoutDate != null && workoutDate.before(today.time)
            } catch (e: Exception) {
                false
            }
        }

        val futureWorkouts = workouts.filter { workout ->
            try {
                val workoutDate = dateFormat.parse(workout.date)
                workoutDate != null && workoutDate.after(today.time)
            } catch (e: Exception) {
                false
            }
        }

        // Můžeme přidat summary informaci
    }

    private fun showWorkoutsForDate(selectedDate: String) {
        val workoutsForDay = allWorkouts.filter { it.date == selectedDate }

        if (workoutsForDay.isNotEmpty()) {
            val workoutInfo = StringBuilder()
            workoutInfo.append("📅 $selectedDate\n\n")

            workoutsForDay.forEachIndexed { index, workout ->
                workoutInfo.append("${index + 1}. ${workout.workoutType.displayName}: ${workout.name}\n")
                workoutInfo.append("   ⏱️ ${workout.getAdditionalInfo()}\n")
                workoutInfo.append("   💪 Intenzita: ${workout.intensity}\n")

                if (workout.isCompleted) {
                    workoutInfo.append("   ✅ Dokončeno\n")
                } else {
                    workoutInfo.append("   ⏳ Nedokončeno\n")
                }

                if (workout.description.isNotEmpty()) {
                    workoutInfo.append("   📝 ${workout.description}\n")
                }

                workoutInfo.append("\n")
            }

            binding.tvSelectedDateWorkouts.text = workoutInfo.toString()
        } else {
            binding.tvSelectedDateWorkouts.text = "Žádné tréninky pro $selectedDate"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}