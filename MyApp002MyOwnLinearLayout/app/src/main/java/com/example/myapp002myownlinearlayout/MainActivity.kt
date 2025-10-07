package com.example.myapp002myownlinearlayout

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Najdeme všechny prvky
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)
        val btnClear = findViewById<Button>(R.id.btnClear)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        // Tlačítko VYPOČÍTAT
        btnCalculate.setOnClickListener {
            val weight = etWeight.text.toString()
            val height = etHeight.text.toString()

            if (weight.isNotEmpty() && height.isNotEmpty()) {
                // Převedeme na čísla
                val w = weight.toDouble()
                val h = height.toDouble() / 100 // cm na metry

                // Výpočet BMI = váha / (výška * výška)
                val bmi = w / (h * h)

                // Zobrazíme výsledek
                tvResult.text = "Vaše BMI: %.1f".format(bmi)
            } else {
                tvResult.text = "Vyplňte všechna pole!"
            }
        }

        // Tlačítko VYMAZAT
        btnClear.setOnClickListener {
            etWeight.text.clear()
            etHeight.text.clear()
            tvResult.text = "Zde se zobrazí výsledek"
        }
    }
}
