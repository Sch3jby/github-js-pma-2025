package com.example.codequizmaster.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.codequizmaster.databinding.ActivityMainBinding
import com.example.codequizmaster.ui.login.LoginActivity
import com.example.codequizmaster.ui.quiz.QuizActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var userId: Long = -1

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getLongExtra(EXTRA_USER_ID, -1)
        if (userId == -1L) {
            navigateToLogin()
            return
        }

        setupUI()
        loadUserData()
    }

    private fun setupUI() {
        binding.btnEasy.setOnClickListener {
            startQuiz("EASY")
        }

        binding.btnMedium.setOnClickListener {
            startQuiz("MEDIUM")
        }

        binding.btnHard.setOnClickListener {
            startQuiz("HARD")
        }

        binding.btnHistory.setOnClickListener {
            // TODO: Implementovat historii
        }

        binding.btnLogout.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun loadUserData() {
        viewModel.loadUser(userId)

        viewModel.observeUser(userId).observe(this) { user ->
            user?.let {
                binding.tvUsername.text = it.username
                binding.tvTotalScore.text = it.totalScore.toString()
                binding.tvGamesPlayed.text = it.totalGamesPlayed.toString()
                binding.tvHighestScore.text = it.highestScore.toString()
            }
        }
    }

    private fun startQuiz(difficulty: String) {
        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra(QuizActivity.EXTRA_USER_ID, userId)
            putExtra(QuizActivity.EXTRA_DIFFICULTY, difficulty)
        }
        startActivity(intent)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}