package com.example.christmascountdown.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.christmascountdown.data.UserPreferencesManager
import com.example.christmascountdown.databinding.FragmentCountdownBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit

class CountdownFragment : Fragment() {

    private var _binding: FragmentCountdownBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountdownBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserName()
        startCountdown()
    }

    private fun loadUserName() {
        viewLifecycleOwner.lifecycleScope.launch {
            UserPreferencesManager.getUserName(requireContext()).collect { name ->
                if (name.isNotEmpty()) {
                    binding.tvGreeting.text = "🎄 Hello, $name! 🎄"
                } else {
                    binding.tvGreeting.text = "🎄 Christmas is coming! 🎄"
                }
            }
        }
    }

    private fun startCountdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                updateCountdown()
                delay(1000) // Update každou sekundu
            }
        }
    }

    private fun updateCountdown() {
        val now = Calendar.getInstance()
        val christmas = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 25)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // Pokud už Vánoce letos proběhly, nastavit příští rok
            if (timeInMillis < now.timeInMillis) {
                add(Calendar.YEAR, 1)
            }
        }

        val diff = christmas.timeInMillis - now.timeInMillis

        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff) % 60

        binding.tvDays.text = days.toString()
        binding.tvHours.text = String.format("%02d", hours)
        binding.tvMinutes.text = String.format("%02d", minutes)
        binding.tvSeconds.text = String.format("%02d", seconds)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}