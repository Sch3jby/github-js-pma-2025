package com.example.semestralproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.semestralproject.databinding.FragmentProfileBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isEditMode = false
    private val weightRecords = mutableListOf<WeightRecord>()
    private lateinit var weightAdapter: WeightRecordAdapter

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
        loadWeightRecords()
        setupListeners()
        updateDisplayMode()
        updateWeightChart()
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

        // Načtení dat do editačních polí
        binding.etName.setText(prefs.getString("name", ""))
        binding.etAge.setText(prefs.getString("age", ""))
        binding.etEmail.setText(prefs.getString("email", ""))
        binding.etPhone.setText(prefs.getString("phone", ""))
        binding.etHeight.setText(prefs.getString("height", ""))

        val gender = prefs.getString("gender", "Muž")
        when (gender) {
            "Muž" -> binding.radioMale.isChecked = true
            "Žena" -> binding.radioFemale.isChecked = true
            "Jiné" -> binding.radioOther.isChecked = true
        }
    }

    private fun loadWeightRecords() {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("weight_records", "[]")
        val type = object : TypeToken<List<WeightRecord>>() {}.type
        val records: List<WeightRecord> = Gson().fromJson(json, type)

        weightRecords.clear()
        weightRecords.addAll(records.sortedByDescending { it.timestamp })

        setupWeightAdapter()
    }

    private fun setupWeightAdapter() {
        weightAdapter = WeightRecordAdapter(
            requireContext(),
            weightRecords,
            onDeleteClick = { record, position ->
                deleteWeightRecord(record, position)
            }
        )
        binding.listViewWeightHistory.adapter = weightAdapter
    }

    private fun saveWeightRecords() {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(weightRecords)
        prefs.edit().putString("weight_records", json).apply()
    }

    private fun setupListeners() {
        // Přepínání mezi režimy
        binding.ivEditProfile.setOnClickListener {
            isEditMode = !isEditMode
            updateDisplayMode()
        }

        // Uložení profilu
        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }

        // Přidání záznamu hmotnosti
        binding.btnAddWeight.setOnClickListener {
            addWeightRecord()
        }
    }

    private fun updateDisplayMode() {
        if (isEditMode) {
            // Režim editace
            binding.layoutViewMode.visibility = View.GONE
            binding.layoutEditMode.visibility = View.VISIBLE
            binding.ivEditProfile.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        } else {
            // Režim zobrazení
            binding.layoutViewMode.visibility = View.VISIBLE
            binding.layoutEditMode.visibility = View.GONE
            binding.ivEditProfile.setImageResource(android.R.drawable.ic_menu_edit)

            updateProfileDisplay()
        }
    }

    private fun updateProfileDisplay() {
        val prefs = requireContext().getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

        val name = prefs.getString("name", "") ?: ""
        val age = prefs.getString("age", "") ?: ""
        val gender = prefs.getString("gender", "Muž") ?: "Muž"
        val email = prefs.getString("email", "") ?: ""
        val phone = prefs.getString("phone", "") ?: ""
        val height = prefs.getString("height", "") ?: ""

        // Aktualizace zobrazení
        binding.tvNameDisplay.text = if (name.isNotEmpty()) name else "Jméno neuvedeno"
        binding.tvAgeDisplay.text = if (age.isNotEmpty()) "Věk: $age let" else "Věk: -"
        binding.tvGenderDisplay.text = "Pohlaví: $gender"
        binding.tvEmailDisplay.text = if (email.isNotEmpty()) "📧 $email" else "📧 Email neuvedeno"
        binding.tvPhoneDisplay.text = if (phone.isNotEmpty()) "📱 $phone" else "📱 Telefon neuvedeno"
        binding.tvHeightDisplay.text = if (height.isNotEmpty()) "📏 Výška: $height cm" else "📏 Výška: - cm"

        // Zobrazení aktuální váhy
        val currentWeight = weightRecords.firstOrNull()?.weight
        binding.tvWeightDisplay.text = if (currentWeight != null) {
            "⚖️ Váha: ${String.format("%.1f", currentWeight)} kg"
        } else {
            "⚖️ Váha: - kg"
        }

        // Výpočet BMI
        if (height.isNotEmpty() && currentWeight != null) {
            calculateAndDisplayBMI(currentWeight, height.toFloatOrNull())
        } else {
            binding.tvBMIDisplay.visibility = View.GONE
        }
    }

    private fun calculateAndDisplayBMI(weight: Float, height: Float?) {
        if (height != null && height > 0) {
            val heightInMeters = height / 100
            val bmi = weight / (heightInMeters * heightInMeters)

            val bmiCategory = when {
                bmi < 18.5 -> "Podváha"
                bmi < 25 -> "Normální váha"
                bmi < 30 -> "Nadváha"
                else -> "Obezita"
            }

            val color = when {
                bmi < 18.5 -> android.graphics.Color.parseColor("#FF9800")
                bmi < 25 -> android.graphics.Color.parseColor("#4CAF50")
                bmi < 30 -> android.graphics.Color.parseColor("#FF9800")
                else -> android.graphics.Color.parseColor("#F44336")
            }

            binding.tvBMIDisplay.text = String.format("💪 BMI: %.1f (%s)", bmi, bmiCategory)
            binding.tvBMIDisplay.setTextColor(color)
            binding.tvBMIDisplay.visibility = View.VISIBLE
        }
    }

    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val age = binding.etAge.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
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
            putString("height", height)
            putString("gender", gender)
            apply()
        }

        Toast.makeText(requireContext(), "Profil uložen", Toast.LENGTH_SHORT).show()

        isEditMode = false
        updateDisplayMode()
    }

    private fun addWeightRecord() {
        val weightStr = binding.etCurrentWeight.text.toString().trim()

        if (weightStr.isEmpty()) {
            Toast.makeText(requireContext(), "Zadejte hmotnost", Toast.LENGTH_SHORT).show()
            return
        }

        val weight = weightStr.toFloatOrNull()
        if (weight == null || weight <= 0) {
            Toast.makeText(requireContext(), "Zadejte platnou hmotnost", Toast.LENGTH_SHORT).show()
            return
        }

        val newRecord = WeightRecord.create(weight)
        weightRecords.add(0, newRecord)

        saveWeightRecords()
        weightAdapter.notifyDataSetChanged()

        binding.etCurrentWeight.setText("")
        updateWeightChart()
        updateProfileDisplay()

        Toast.makeText(requireContext(), "Záznam přidán", Toast.LENGTH_SHORT).show()
    }

    private fun deleteWeightRecord(record: WeightRecord, position: Int) {
        weightRecords.removeAt(position)
        saveWeightRecords()
        weightAdapter.notifyDataSetChanged()
        updateWeightChart()
        updateProfileDisplay()

        Toast.makeText(requireContext(), "Záznam smazán", Toast.LENGTH_SHORT).show()
    }

    private fun updateWeightChart() {
        if (weightRecords.isEmpty()) {
            binding.tvNoWeightData.visibility = View.VISIBLE
            binding.layoutWeightChart.visibility = View.GONE
            binding.cardWeightHistory.visibility = View.GONE
            binding.tvWeightStats.visibility = View.GONE
        } else {
            binding.tvNoWeightData.visibility = View.GONE
            binding.layoutWeightChart.visibility = View.VISIBLE
            binding.cardWeightHistory.visibility = View.VISIBLE
            binding.tvWeightStats.visibility = View.VISIBLE

            // Aktualizace grafu
            val sortedRecords = weightRecords.sortedBy { it.timestamp }
            binding.weightChartView.setData(sortedRecords)

            // Statistiky
            updateWeightStatistics(sortedRecords)
        }
    }

    private fun updateWeightStatistics(sortedRecords: List<WeightRecord>) {
        if (sortedRecords.size < 2) {
            binding.tvWeightStats.text = "První záznam! Přidejte další záznamy pro sledování vývoje."
            return
        }

        val firstWeight = sortedRecords.first().weight
        val lastWeight = sortedRecords.last().weight
        val change = lastWeight - firstWeight

        val avgWeight = sortedRecords.map { it.weight }.average().toFloat()
        val minWeight = sortedRecords.minOf { it.weight }
        val maxWeight = sortedRecords.maxOf { it.weight }

        val changeText = if (change > 0) {
            "+${String.format("%.1f", change)} kg (přírůstek)"
        } else if (change < 0) {
            "${String.format("%.1f", change)} kg (úbytek)"
        } else {
            "Beze změny"
        }

        val statsText = """
            📊 Celková změna: $changeText
            📈 Průměr: ${String.format("%.1f", avgWeight)} kg
            🔽 Minimum: ${String.format("%.1f", minWeight)} kg
            🔼 Maximum: ${String.format("%.1f", maxWeight)} kg
        """.trimIndent()

        binding.tvWeightStats.text = statsText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Adapter pro seznam záznamů hmotnosti
    private inner class WeightRecordAdapter(
        context: Context,
        private val records: List<WeightRecord>,
        private val onDeleteClick: (WeightRecord, Int) -> Unit
    ) : ArrayAdapter<WeightRecord>(context, 0, records) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_weight_record, parent, false)

            val record = records[position]

            val tvWeightValue = view.findViewById<TextView>(R.id.tvWeightValue)
            val tvWeightDate = view.findViewById<TextView>(R.id.tvWeightDate)
            val tvWeightChange = view.findViewById<TextView>(R.id.tvWeightChange)
            val btnDelete = view.findViewById<android.widget.Button>(R.id.btnDeleteWeight)

            tvWeightValue.text = "${String.format("%.1f", record.weight)} kg"
            tvWeightDate.text = record.date

            // Výpočet změny oproti předchozímu záznamu
            if (position < records.size - 1) {
                val previousWeight = records[position + 1].weight
                val change = record.weight - previousWeight

                if (change > 0) {
                    tvWeightChange.text = "+${String.format("%.1f", change)} kg"
                    tvWeightChange.setTextColor(android.graphics.Color.parseColor("#F44336"))
                } else if (change < 0) {
                    tvWeightChange.text = "${String.format("%.1f", change)} kg"
                    tvWeightChange.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
                } else {
                    tvWeightChange.text = "0.0 kg"
                    tvWeightChange.setTextColor(android.graphics.Color.GRAY)
                }
            } else {
                tvWeightChange.text = "První"
                tvWeightChange.setTextColor(android.graphics.Color.GRAY)
            }

            btnDelete.setOnClickListener {
                onDeleteClick(record, position)
            }

            return view
        }
    }
}