package com.example.semestralproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.semestralproject.databinding.FragmentStatisticsBinding
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseRepository = FirebaseRepository()

        observeWorkouts()
    }

    private fun observeWorkouts() {
        lifecycleScope.launch {
            firebaseRepository.getWorkoutsFlow().collect { workouts ->
                calculateStatistics(workouts)
            }
        }
    }

    private fun calculateStatistics(workouts: List<Workout>) {
        if (workouts.isEmpty()) {
            binding.layoutEmptyState.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
            return
        }

        binding.layoutEmptyState.visibility = View.GONE
        binding.scrollView.visibility = View.VISIBLE

        // Celkové statistiky
        val totalWorkouts = workouts.size
        val completedWorkouts = workouts.count { it.isCompleted }
        val totalMinutes = workouts.sumOf { it.duration }
        val totalHours = totalMinutes / 60.0

        binding.tvTotalWorkouts.text = "$totalWorkouts"
        binding.tvCompletedWorkouts.text = "$completedWorkouts"
        binding.tvTotalTime.text = String.format("%.1f hodin", totalHours)

        // Statistiky podle typu aktivity
        val statisticsByType = StringBuilder()

        WorkoutType.values().forEach { type ->
            val workoutsOfType = workouts.filter { it.workoutType == type }

            if (workoutsOfType.isNotEmpty()) {
                statisticsByType.append("\n━━━━━━━━━━━━━━━━━━━━━\n")
                statisticsByType.append("${type.displayName}\n")
                statisticsByType.append("━━━━━━━━━━━━━━━━━━━━━\n")

                val count = workoutsOfType.size
                val totalTime = workoutsOfType.sumOf { it.duration }
                val avgTime = totalTime / count
                val completed = workoutsOfType.count { it.isCompleted }

                statisticsByType.append("📊 Počet: $count\n")
                statisticsByType.append("⏱️ Celkový čas: $totalTime min\n")
                statisticsByType.append("📈 Průměrný čas: $avgTime min\n")
                statisticsByType.append("✅ Dokončeno: $completed\n")

                // Pokud má aktivita vzdálenost
                if (type.fields.contains(WorkoutField.DISTANCE)) {
                    val workoutsWithDistance = workoutsOfType.filter { it.distance != null && it.distance!! > 0 }

                    if (workoutsWithDistance.isNotEmpty()) {
                        val totalDistance = workoutsWithDistance.sumOf { it.distance!!.toDouble() }
                        val avgDistance = totalDistance / workoutsWithDistance.size
                        val avgSpeed = if (totalTime > 0) (totalDistance / totalTime) * 60 else 0.0

                        statisticsByType.append(String.format("🏃 Celková vzdálenost: %.2f km\n", totalDistance))
                        statisticsByType.append(String.format("📏 Průměrná vzdálenost: %.2f km\n", avgDistance))
                        statisticsByType.append(String.format("⚡ Průměrná rychlost: %.2f km/h\n", avgSpeed))
                    }
                }

                statisticsByType.append("\n")
            }
        }

        binding.tvStatisticsByType.text = statisticsByType.toString()

        // Statistiky podle intenzity
        val lowIntensity = workouts.count { it.intensity == "Nízká" }
        val mediumIntensity = workouts.count { it.intensity == "Střední" }
        val highIntensity = workouts.count { it.intensity == "Vysoká" }

        val intensityStats = StringBuilder()
        intensityStats.append("💪 Podle intenzity:\n\n")
        intensityStats.append("🟢 Nízká: $lowIntensity (${(lowIntensity * 100.0 / totalWorkouts).roundToInt()}%)\n\n")
        intensityStats.append("🟡 Střední: $mediumIntensity (${(mediumIntensity * 100.0 / totalWorkouts).roundToInt()}%)\n\n")
        intensityStats.append("🔴 Vysoká: $highIntensity (${(highIntensity * 100.0 / totalWorkouts).roundToInt()}%)\n")

        binding.tvIntensityStats.text = intensityStats.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}