package com.example.myapp010ahadejcislo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var etUserGuess: EditText
    private lateinit var btnGuess: Button
    private lateinit var btnReset: Button
    private lateinit var tvScore: TextView
    private lateinit var tvWins: TextView

    private var secretNumber = 0
    private var attempts = 0
    private var wins = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        generateNewNumber()
        setupListeners()
    }

    private fun initViews() {
        etUserGuess = findViewById(R.id.etUserGuess)
        btnGuess = findViewById(R.id.btnGuess)
        btnReset = findViewById(R.id.btnReset)
        tvScore = findViewById(R.id.tvScore)
        tvWins = findViewById(R.id.tvWins)
    }

    private fun setupListeners() {
        btnGuess.setOnClickListener {
            checkGuess()
        }

        btnReset.setOnClickListener {
            resetGame()
        }

        // Možnost tipnout ENTERem
        etUserGuess.setOnEditorActionListener { _, _, _ ->
            checkGuess()
            true
        }
    }

    private fun generateNewNumber() {
        secretNumber = Random.nextInt(1, 11) // 1 až 10
    }

    private fun checkGuess() {
        val userInput = etUserGuess.text.toString()

        if (userInput.isEmpty()) {
            Toast.makeText(this, "Zadej číslo!", Toast.LENGTH_SHORT).show()
            return
        }

        val userGuess = userInput.toIntOrNull()

        if (userGuess == null || userGuess !in 1..10) {
            Toast.makeText(this, "Zadej číslo od 1 do 10!", Toast.LENGTH_SHORT).show()
            return
        }

        attempts++
        updateScore()

        when {
            userGuess == secretNumber -> {
                // VÝHRA!
                Toast.makeText(this, "🎉 Správně! Číslo bylo $secretNumber", Toast.LENGTH_LONG).show()
                wins++
                updateWins()
                generateNewNumber()
                attempts = 0
                updateScore()
                etUserGuess.text.clear()
            }
            userGuess < secretNumber -> {
                Toast.makeText(this, "📈 Větší!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "📉 Menší!", Toast.LENGTH_SHORT).show()
            }
        }

        etUserGuess.requestFocus()
    }

    private fun resetGame() {
        generateNewNumber()
        attempts = 0
        wins = 0
        updateScore()
        updateWins()
        etUserGuess.text.clear()
        Toast.makeText(this, "Nová hra začíná!", Toast.LENGTH_SHORT).show()
    }

    private fun updateScore() {
        tvScore.text = "Pokusy: $attempts"
    }

    private fun updateWins() {
        tvWins.text = "Výhry: $wins"
    }
}