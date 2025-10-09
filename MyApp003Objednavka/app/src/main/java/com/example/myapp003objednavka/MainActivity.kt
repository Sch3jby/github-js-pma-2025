package com.example.myapp003objednavka

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp003objednavka.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavíme výchozí obrázek
        updateBikeImage()

        // Listener pro změnu modelu kola - automaticky zobrazuje souhrn
        binding.rgBikeModel.setOnCheckedChangeListener { _, _ ->
            updateBikeImage()
            showOrderSummary()
        }

        // Listenery pro checkboxy - automaticky zobrazují souhrn
        binding.cbBetterFork.setOnCheckedChangeListener { _, _ -> showOrderSummary() }
        binding.cbBetterSeat.setOnCheckedChangeListener { _, _ -> showOrderSummary() }
        binding.cbCarbonFrame.setOnCheckedChangeListener { _, _ -> showOrderSummary() }

        // Tlačítko Objednat - zobrazí "Úspěšně objednáno" a skryje souhrn
        binding.btnOrder.setOnClickListener {
            // Skryjeme nadpis a obrázek
            binding.tvSummaryTitle.text = "Úspěšně objednáno!"
            binding.tvSummaryTitle.textSize = 24f
            binding.tvSummaryTitle.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
            binding.ivBike.visibility = View.GONE
        }

        // Zobrazíme počáteční souhrn
        showOrderSummary()
    }

    private fun updateBikeImage() {
        when (binding.rgBikeModel.checkedRadioButtonId) {
            R.id.rbM10 -> binding.ivBike.setImageResource(R.drawable.oiz_m10_tr)
            R.id.rbM20 -> binding.ivBike.setImageResource(R.drawable.oiz_m20_tr)
            R.id.rbM30 -> binding.ivBike.setImageResource(R.drawable.oiz_m30_tr)
        }
    }

    private fun showOrderSummary() {
        // Pokud už bylo objednáno, neměníme text
        if (binding.tvSummaryTitle.text == "Úspěšně objednáno!") {
            return
        }

        val modelName = when (binding.rgBikeModel.checkedRadioButtonId) {
            R.id.rbM10 -> "OIZ M10"
            R.id.rbM20 -> "OIZ M20"
            R.id.rbM30 -> "OIZ M30"
            else -> "Nevybráno"
        }

        val extras = mutableListOf<String>()
        if (binding.cbBetterFork.isChecked) extras.add("lepší vidlice")
        if (binding.cbBetterSeat.isChecked) extras.add("lepší sedlo")
        if (binding.cbCarbonFrame.isChecked) extras.add("karbonová řidítka")

        // Sestavíme text souhrnu
        val summaryText = if (extras.isEmpty()) {
            "Model: $modelName"
        } else {
            "Model: $modelName\nPříslušenství: ${extras.joinToString(", ")}"
        }

        binding.tvSummaryTitle.text = "Souhrn objednávky:\n$summaryText"
    }
}