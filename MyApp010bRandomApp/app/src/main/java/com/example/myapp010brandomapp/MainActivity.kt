package com.example.myapp010brandomapp

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var btnD6: Button
    private lateinit var btnD20: Button
    private lateinit var btnD100: Button
    private lateinit var btnMinus: Button
    private lateinit var btnPlus: Button
    private lateinit var btnRoll: Button
    private lateinit var btnReset: Button
    private lateinit var tvDiceCount: TextView
    private lateinit var tvResult: TextView
    private lateinit var tvDiceValues: TextView
    private lateinit var tvStats: TextView
    private lateinit var tvHistory: TextView

    private var diceType = 6 // D6, D20, nebo D100
    private var diceCount = 1

    private val history = mutableListOf<RollResult>()
    private var totalRolls = 0
    private var sumOfAllRolls = 0
    private var highestRoll = 0
    private var lowestRoll = Int.MAX_VALUE

    data class RollResult(val diceType: Int, val count: Int, val values: List<Int>, val sum: Int)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        updateDiceTypeButtons()
    }

    private fun initViews() {
        btnD6 = findViewById(R.id.btnD6)
        btnD20 = findViewById(R.id.btnD20)
        btnD100 = findViewById(R.id.btnD100)
        btnMinus = findViewById(R.id.btnMinus)
        btnPlus = findViewById(R.id.btnPlus)
        btnRoll = findViewById(R.id.btnRoll)
        btnReset = findViewById(R.id.btnReset)
        tvDiceCount = findViewById(R.id.tvDiceCount)
        tvResult = findViewById(R.id.tvResult)
        tvDiceValues = findViewById(R.id.tvDiceValues)
        tvStats = findViewById(R.id.tvStats)
        tvHistory = findViewById(R.id.tvHistory)
    }

    private fun setupListeners() {
        btnD6.setOnClickListener {
            diceType = 6
            updateDiceTypeButtons()
        }

        btnD20.setOnClickListener {
            diceType = 20
            updateDiceTypeButtons()
        }

        btnD100.setOnClickListener {
            diceType = 100
            updateDiceTypeButtons()
        }

        btnMinus.setOnClickListener {
            if (diceCount > 1) {
                diceCount--
                tvDiceCount.text = diceCount.toString()
            }
        }

        btnPlus.setOnClickListener {
            if (diceCount < 6) {
                diceCount++
                tvDiceCount.text = diceCount.toString()
            } else {
                Toast.makeText(this, "Maximum je 6 kostek!", Toast.LENGTH_SHORT).show()
            }
        }

        btnRoll.setOnClickListener {
            rollDice()
        }

        btnReset.setOnClickListener {
            resetStats()
        }
    }

    private fun updateDiceTypeButtons() {
        // Reset všech barev
        btnD6.alpha = 0.5f
        btnD20.alpha = 0.5f
        btnD100.alpha = 0.5f

        // Zvýrazni vybranou
        when (diceType) {
            6 -> btnD6.alpha = 1.0f
            20 -> btnD20.alpha = 1.0f
            100 -> btnD100.alpha = 1.0f
        }
    }

    private fun rollDice() {
        // Animace tlačítka
        animateButton(btnRoll)

        // Házení kostek
        val values = mutableListOf<Int>()
        repeat(diceCount) {
            val roll = Random.nextInt(1, diceType + 1)
            values.add(roll)
        }

        val sum = values.sum()

        // Animace výsledku
        animateResult(sum)

        // Zobrazení jednotlivých hodnot
        if (diceCount > 1) {
            tvDiceValues.text = "Kostky: ${values.joinToString(", ")}"
        } else {
            tvDiceValues.text = ""
        }

        // Aktualizace statistik
        updateStats(sum)

        // Přidání do historie
        val rollResult = RollResult(diceType, diceCount, values, sum)
        history.add(0, rollResult)
        if (history.size > 5) {
            history.removeAt(5)
        }
        updateHistory()
    }

    private fun animateButton(button: Button) {
        val scaleDown = ObjectAnimator.ofFloat(button, "scaleX", 1f, 0.9f)
        scaleDown.duration = 100
        scaleDown.repeatCount = 1
        scaleDown.repeatMode = ObjectAnimator.REVERSE
        scaleDown.start()

        val scaleDownY = ObjectAnimator.ofFloat(button, "scaleY", 1f, 0.9f)
        scaleDownY.duration = 100
        scaleDownY.repeatCount = 1
        scaleDownY.repeatMode = ObjectAnimator.REVERSE
        scaleDownY.start()
    }

    private fun animateResult(finalValue: Int) {
        // Rychlá animace změny čísla
        val animator = ObjectAnimator.ofFloat(tvResult, "alpha", 1f, 0f, 1f)
        animator.duration = 200
        animator.start()

        tvResult.postDelayed({
            tvResult.text = finalValue.toString()
        }, 100)
    }

    private fun updateStats(roll: Int) {
        totalRolls++
        sumOfAllRolls += roll

        if (roll > highestRoll) highestRoll = roll
        if (roll < lowestRoll) lowestRoll = roll

        val average = sumOfAllRolls.toFloat() / totalRolls

        tvStats.text = """
            Počet hodů: $totalRolls
            Průměr: ${"%.1f".format(average)}
            Nejvyšší: $highestRoll
            Nejnižší: $lowestRoll
        """.trimIndent()
    }

    private fun updateHistory() {
        if (history.isEmpty()) {
            tvHistory.text = "Zatím žádné hody..."
            return
        }

        val historyText = history.joinToString("\n") { roll ->
            "${roll.count}× D${roll.diceType} = ${roll.sum} (${roll.values.joinToString(", ")})"
        }

        tvHistory.text = historyText
    }

    private fun resetStats() {
        totalRolls = 0
        sumOfAllRolls = 0
        highestRoll = 0
        lowestRoll = Int.MAX_VALUE
        history.clear()

        tvResult.text = "---"
        tvDiceValues.text = ""
        tvStats.text = "Počet hodů: 0\nPrůměr: ---\nNejvyšší: ---\nNejnižší: ---"
        tvHistory.text = "Zatím žádné hody..."

        Toast.makeText(this, "Statistiky resetovány!", Toast.LENGTH_SHORT).show()
    }
}