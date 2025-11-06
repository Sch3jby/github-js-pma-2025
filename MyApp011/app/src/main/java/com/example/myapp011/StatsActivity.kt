package com.example.myapp011

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp011.databinding.ActivityStatsBinding
import com.google.android.material.snackbar.Snackbar

class StatsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavit Action Bar
        supportActionBar?.title = "Statistiky"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Načíst a zobrazit statistiky
        loadStats()

        // Tlačítko pro reset statistik
        binding.btnResetStats.setOnClickListener {
            resetStatsWithUndo()
        }
    }

    private fun loadStats() {
        val prefs = getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)

        // Celkové skóre
        val totalScore = prefs.getInt("total_score", 0)
        binding.tvTotalScore.text = "$totalScore bodů"

        // Sport
        val sportScore = prefs.getInt("sport_score", 0)
        val sportTotal = prefs.getInt("sport_total", 0)
        binding.tvSportScore.text = "$sportScore/$sportTotal"

        // Historie
        val historyScore = prefs.getInt("historie_score", 0)
        val historyTotal = prefs.getInt("historie_total", 0)
        binding.tvHistoryScore.text = "$historyScore/$historyTotal"

        // Věda
        val scienceScore = prefs.getInt("věda_score", 0)
        val scienceTotal = prefs.getInt("věda_total", 0)
        binding.tvScienceScore.text = "$scienceScore/$scienceTotal"

        // Filmy
        val moviesScore = prefs.getInt("filmy_score", 0)
        val moviesTotal = prefs.getInt("filmy_total", 0)
        binding.tvMoviesScore.text = "$moviesScore/$moviesTotal"
    }

    private fun resetStatsWithUndo() {
        val prefs = getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)

        // Uložit staré hodnoty pro UNDO
        val oldTotalScore = prefs.getInt("total_score", 0)
        val oldSportScore = prefs.getInt("sport_score", 0)
        val oldSportTotal = prefs.getInt("sport_total", 0)
        val oldHistoryScore = prefs.getInt("historie_score", 0)
        val oldHistoryTotal = prefs.getInt("historie_total", 0)
        val oldScienceScore = prefs.getInt("věda_score", 0)
        val oldScienceTotal = prefs.getInt("věda_total", 0)
        val oldMoviesScore = prefs.getInt("filmy_score", 0)
        val oldMoviesTotal = prefs.getInt("filmy_total", 0)
        val oldQuizzesPlayed = prefs.getInt("quizzes_played", 0)

        // Resetovat statistiky
        val editor = prefs.edit()
        editor.putInt("total_score", 0)
        editor.putInt("sport_score", 0)
        editor.putInt("sport_total", 0)
        editor.putInt("historie_score", 0)
        editor.putInt("historie_total", 0)
        editor.putInt("věda_score", 0)
        editor.putInt("věda_total", 0)
        editor.putInt("filmy_score", 0)
        editor.putInt("filmy_total", 0)
        editor.putInt("quizzes_played", 0)
        editor.apply()

        // Aktualizovat zobrazení
        loadStats()

        // Snackbar s UNDO akcí
        Snackbar.make(binding.root, "Statistiky resetovány", Snackbar.LENGTH_LONG)
            .setAction("VRÁTIT ZPĚT") {
                // Obnovit staré hodnoty
                val restoreEditor = prefs.edit()
                restoreEditor.putInt("total_score", oldTotalScore)
                restoreEditor.putInt("sport_score", oldSportScore)
                restoreEditor.putInt("sport_total", oldSportTotal)
                restoreEditor.putInt("historie_score", oldHistoryScore)
                restoreEditor.putInt("historie_total", oldHistoryTotal)
                restoreEditor.putInt("věda_score", oldScienceScore)
                restoreEditor.putInt("věda_total", oldScienceTotal)
                restoreEditor.putInt("filmy_score", oldMoviesScore)
                restoreEditor.putInt("filmy_total", oldMoviesTotal)
                restoreEditor.putInt("quizzes_played", oldQuizzesPlayed)
                restoreEditor.apply()

                // Aktualizovat zobrazení
                loadStats()

                Snackbar.make(binding.root, "Statistiky obnoveny ✅", Snackbar.LENGTH_SHORT).show()
            }
            .show()
    }

    // Zpět tlačítko v Action Bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}