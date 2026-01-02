package com.example.semestralproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.semestralproject.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()
        setupListeners()
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

        binding.etName.setText(prefs.getString("name", ""))
        binding.etAge.setText(prefs.getString("age", ""))
        binding.etEmail.setText(prefs.getString("email", ""))
        binding.etPhone.setText(prefs.getString("phone", ""))
        binding.etWeight.setText(prefs.getString("weight", ""))
        binding.etHeight.setText(prefs.getString("height", ""))

        val gender = prefs.getString("gender", "Muž")
        when (gender) {
            "Muž" -> binding.radioMale.isChecked = true
            "Žena" -> binding.radioFemale.isChecked = true
            "Jiné" -> binding.radioOther.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val weight = binding.etWeight.text.toString().trim()
        val height = binding.etHeight.text.toString().trim()

        val gender = when (binding.radioGroupGender.checkedRadioButtonId) {
            R.id.radioMale -> "Muž"
            R.id.radioFemale -> "Žena"
            R.id.radioOther -> "Jiné"
            else -> "Muž"
        }

        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("name", name)
            putString("age", age)
            putString("email", email)
            putString("phone", phone)
            putString("weight", weight)
            putString("height", height)
            putString("gender", gender)
            apply()
        }

        Toast.makeText(requireContext(), "Profil uložen", Toast.LENGTH_SHORT).show()

        // Vypočítáme BMI pokud jsou zadány výška a váha
        if (weight.isNotEmpty() && height.isNotEmpty()) {
            calculateBMI(weight.toFloatOrNull(), height.toFloatOrNull())
        }
    }

    private fun calculateBMI(weight: Float?, height: Float?) {
        if (weight != null && height != null && height > 0) {
            val heightInMeters = height / 100
            val bmi = weight / (heightInMeters * heightInMeters)

            val bmiCategory = when {
                bmi < 18.5 -> "Podváha"
                bmi < 25 -> "Normální váha"
                bmi < 30 -> "Nadváha"
                else -> "Obezita"
            }

            binding.tvBMI.text = String.format("BMI: %.1f (%s)", bmi, bmiCategory)
            binding.tvBMI.visibility = View.VISIBLE
        } else {
            binding.tvBMI.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}