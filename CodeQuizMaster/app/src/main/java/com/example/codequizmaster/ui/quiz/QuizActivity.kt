package com.example.codequizmaster.ui.quiz

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.codequizmaster.databinding.ActivityQuizBinding
import com.example.codequizmaster.ui.main.MainActivity
import com.google.android.material.button.MaterialButton

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()

    private var userId: Long = -1
    private var difficulty: String = "EASY"
    private lateinit var answerButtons: List<MaterialButton>

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
        const val EXTRA_DIFFICULTY = "extra_difficulty"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getLongExtra(EXTRA_USER_ID, -1)
        difficulty = intent.getStringExtra(EXTRA_DIFFICULTY) ?: "EASY"

        if (userId == -1L) {
            finish()
            return
        }

        answerButtons = listOf(
            binding.btnAnswer1,
            binding.btnAnswer2,
            binding.btnAnswer3,
            binding.btnAnswer4
        )

        setupUI()
        observeViewModel()

        viewModel.startGame(userId, difficulty, 10)
    }

    private fun setupUI() {
        answerButtons.forEach { button ->
            button.setOnClickListener {
                handleAnswerClick(button.text.toString())
            }
        }

        binding.btnNext.setOnClickListener {
            viewModel.nextQuestion()
        }
    }

    private fun observeViewModel() {
        viewModel.questions.observe(this) { questions ->
            if (questions.isEmpty()) {
                showError("Žádné otázky pro tuto obtížnost!")
                return@observe
            }
            displayCurrentQuestion()
        }

        viewModel.currentQuestionIndex.observe(this) { index ->
            updateProgressBar(index)
            displayCurrentQuestion()
        }

        viewModel.score.observe(this) { score ->
            binding.tvScore.text = "Skóre: $score"
        }

        viewModel.answerResult.observe(this) { result ->
            result?.let {
                showAnswerFeedback(it)
            } ?: run {
                hideFeedback()
                enableAnswerButtons()
            }
        }

        viewModel.gameFinished.observe(this) { finished ->
            if (finished) {
                showGameFinishedDialog()
            }
        }
    }

    private fun displayCurrentQuestion() {
        val question = viewModel.getCurrentQuestion() ?: return
        val answers = viewModel.getShuffledAnswers()

        val currentIndex = (viewModel.currentQuestionIndex.value ?: 0) + 1
        val totalQuestions = viewModel.questions.value?.size ?: 0
        binding.tvQuestionNumber.text = "Otázka $currentIndex/$totalQuestions"

        binding.tvQuestion.text = question.questionText

        answerButtons.forEachIndexed { index, button ->
            button.text = answers.getOrNull(index) ?: ""
            button.setBackgroundColor(Color.TRANSPARENT)
            button.isEnabled = true
        }

        hideFeedback()
    }

    private fun handleAnswerClick(selectedAnswer: String) {
        disableAnswerButtons()
        viewModel.submitAnswer(selectedAnswer)
    }

    private fun showAnswerFeedback(result: QuizViewModel.AnswerResult) {
        binding.cvFeedback.visibility = View.VISIBLE

        answerButtons.forEach { button ->
            when (button.text.toString()) {
                result.correctAnswer -> {
                    button.setBackgroundColor(Color.parseColor("#4CAF50"))
                }
            }
        }

        if (result.isCorrect) {
            binding.tvFeedback.text = "✅ Výborně! Správná odpověď!"
            binding.tvFeedback.setTextColor(Color.parseColor("#4CAF50"))
            binding.tvCorrectAnswer.visibility = View.GONE
        } else {
            binding.tvFeedback.text = "❌ Špatně!"
            binding.tvFeedback.setTextColor(Color.parseColor("#F44336"))
            binding.tvCorrectAnswer.visibility = View.VISIBLE
            binding.tvCorrectAnswer.text = "Správná odpověď: ${result.correctAnswer}"
        }

        val currentIndex = viewModel.currentQuestionIndex.value ?: 0
        val totalQuestions = viewModel.questions.value?.size ?: 0

        if (currentIndex + 1 >= totalQuestions) {
            binding.btnNext.text = "Dokončit"
        } else {
            binding.btnNext.text = "Další otázka →"
        }
    }

    private fun hideFeedback() {
        binding.cvFeedback.visibility = View.GONE
    }

    private fun updateProgressBar(index: Int) {
        binding.progressBarQuiz.progress = index + 1
    }

    private fun disableAnswerButtons() {
        answerButtons.forEach { it.isEnabled = false }
    }

    private fun enableAnswerButtons() {
        answerButtons.forEach {
            it.isEnabled = true
            it.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun showGameFinishedDialog() {
        val score = viewModel.score.value ?: 0
        val correct = viewModel.correctAnswersCount.value ?: 0
        val total = viewModel.questions.value?.size ?: 0
        val percentage = if (total > 0) (correct * 100) / total else 0

        val message = """
            Tvé výsledky:
            
            Správných odpovědí: $correct/$total ($percentage%)
            Celkové skóre: $score bodů
            
            ${getEncouragementMessage(percentage)}
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("🎉 Hra dokončena!")
            .setMessage(message)
            .setPositiveButton("Pokračovat") { _, _ ->
                navigateToMain()
            }
            .setCancelable(false)
            .show()
    }

    private fun getEncouragementMessage(percentage: Int): String {
        return when {
            percentage >= 90 -> "Fantastické! Jsi mistr!"
            percentage >= 70 -> "Skvělá práce!"
            percentage >= 50 -> "Dobře, ale můžeš ještě lépe!"
            else -> "Nezdar, ale nevzdávej to!"
        }
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Chyba")
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(MainActivity.EXTRA_USER_ID, userId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Opustit hru?")
            .setMessage("Tvůj postup nebude uložen.")
            .setPositiveButton("Ano") { _, _ ->
                super.onBackPressed()
            }
            .setNegativeButton("Ne", null)
            .show()
    }
}