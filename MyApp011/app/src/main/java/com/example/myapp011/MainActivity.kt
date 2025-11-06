package com.example.myapp011

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp011.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Zobrazit HomeFragment při startu
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainer.id, HomeFragment())
                .commit()
        }

        // Tlačítko pro statistiky
        binding.btnStats.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }

        // Tlačítko pro profil
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    // Funkce pro navigaci mezi fragmenty (volána z fragmentů)
    fun navigateToQuiz(topic: String) {
        val fragment = QuizFragment()
        val bundle = Bundle()
        bundle.putString("topic", topic)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .addToBackStack(null)
            .commit()
    }

    fun navigateToResult(score: Int, total: Int, topic: String) {
        val fragment = ResultFragment()
        val bundle = Bundle()
        bundle.putInt("score", score)
        bundle.putInt("total", total)
        bundle.putString("topic", topic)
        fragment.arguments = bundle

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    fun navigateToHome() {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, HomeFragment())
            .commit()
    }
}