package com.example.myapp010brandomapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var btnD6: Button
    private lateinit var btnD20: Button
    private lateinit var btnD100: Button
    private lateinit var btnRoll: Button
    private lateinit var tvResult: TextView

    private var diceType = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnD6 = findViewById(R.id.btnD6)
        btnD20 = findViewById(R.id.btnD20)
        btnD100 = findViewById(R.id.btnD100)
        btnRoll = findViewById(R.id.btnRoll)
        tvResult = findViewById(R.id.tvResult)

        btnD6.setOnClickListener {
            diceType = 6
            updateButtons()
        }

        btnD20.setOnClickListener {
            diceType = 20
            updateButtons()
        }

        btnD100.setOnClickListener {
            diceType = 100
            updateButtons()
        }

        btnRoll.setOnClickListener {
            val roll = Random.nextInt(1, diceType + 1)
            tvResult.text = roll.toString()
        }

        updateButtons()
    }

    private fun updateButtons() {
        btnD6.alpha = if (diceType == 6) 1.0f else 0.5f
        btnD20.alpha = if (diceType == 20) 1.0f else 0.5f
        btnD100.alpha = if (diceType == 100) 1.0f else 0.5f
    }
}