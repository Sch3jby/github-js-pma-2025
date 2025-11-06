package com.example.myapp011

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp011.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Načíst jméno ze SharedPreferences
        val prefs = requireContext().getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)
        val userName = prefs.getString("user_name", null)

        if (userName != null) {
            binding.tvWelcome.text = "Vítej $userName! 👋"
        }

        // Nastavení tlačítek pro témata
        binding.btnSport.setOnClickListener {
            (activity as MainActivity).navigateToQuiz("Sport")
        }

        binding.btnHistory.setOnClickListener {
            (activity as MainActivity).navigateToQuiz("Historie")
        }

        binding.btnScience.setOnClickListener {
            (activity as MainActivity).navigateToQuiz("Věda")
        }

        binding.btnMovies.setOnClickListener {
            (activity as MainActivity).navigateToQuiz("Filmy")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}