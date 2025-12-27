package com.example.codequizmaster.ui.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.codequizmaster.data.database.QuizDatabase
import com.example.codequizmaster.data.entity.GameSession
import com.example.codequizmaster.data.entity.Question
import com.example.codequizmaster.data.repository.QuizRepository
import kotlinx.coroutines.launch

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _currentQuestionIndex = MutableLiveData<Int>(0)
    val currentQuestionIndex: LiveData<Int> = _currentQuestionIndex

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> = _score

    private val _correctAnswersCount = MutableLiveData<Int>(0)
    val correctAnswersCount: LiveData<Int> = _correctAnswersCount

    private val _gameFinished = MutableLiveData<Boolean>(false)
    val gameFinished: LiveData<Boolean> = _gameFinished

    private val _answerResult = MutableLiveData<AnswerResult?>()
    val answerResult: LiveData<AnswerResult?> = _answerResult

    private var startTime: Long = 0
    private var userId: Long = 0
    private var difficulty: String = ""

    init {
        val database = QuizDatabase.getDatabase(application)
        repository = QuizRepository(
            database.questionDao(),
            database.userDao(),
            database.gameSessionDao()
        )
    }

    fun startGame(userId: Long, difficulty: String, questionCount: Int = 10) {
        this.userId = userId
        this.difficulty = difficulty
        this.startTime = System.currentTimeMillis()

        viewModelScope.launch {
            val loadedQuestions = repository.getRandomQuestions(difficulty, questionCount)
            _questions.value = loadedQuestions
            _currentQuestionIndex.value = 0
            _score.value = 0
            _correctAnswersCount.value = 0
            _gameFinished.value = false
        }
    }

    fun getCurrentQuestion(): Question? {
        val index = _currentQuestionIndex.value ?: return null
        val questionsList = _questions.value ?: return null
        return questionsList.getOrNull(index)
    }

    fun submitAnswer(selectedAnswer: String) {
        val currentQuestion = getCurrentQuestion() ?: return
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        if (isCorrect) {
            _correctAnswersCount.value = (_correctAnswersCount.value ?: 0) + 1
            _score.value = (_score.value ?: 0) + getPointsForDifficulty()
        }

        _answerResult.value = AnswerResult(
            isCorrect = isCorrect,
            correctAnswer = currentQuestion.correctAnswer
        )
    }

    fun nextQuestion() {
        _answerResult.value = null
        val currentIndex = _currentQuestionIndex.value ?: 0
        val totalQuestions = _questions.value?.size ?: 0

        if (currentIndex + 1 < totalQuestions) {
            _currentQuestionIndex.value = currentIndex + 1
        } else {
            finishGame()
        }
    }

    private fun finishGame() {
        _gameFinished.value = true

        viewModelScope.launch {
            val endTime = System.currentTimeMillis()
            val durationSeconds = ((endTime - startTime) / 1000).toInt()
            val finalScore = _score.value ?: 0
            val correct = _correctAnswersCount.value ?: 0
            val total = _questions.value?.size ?: 0

            val session = GameSession(
                userId = userId,
                score = finalScore,
                correctAnswers = correct,
                totalQuestions = total,
                difficulty = difficulty,
                durationSeconds = durationSeconds
            )
            repository.saveGameSession(session)
            repository.updateUserStatsAfterGame(userId, finalScore)
        }
    }

    private fun getPointsForDifficulty(): Int {
        return when (difficulty) {
            "EASY" -> 10
            "MEDIUM" -> 20
            "HARD" -> 30
            else -> 10
        }
    }

    fun getShuffledAnswers(): List<String> {
        val question = getCurrentQuestion() ?: return emptyList()
        return listOf(
            question.correctAnswer,
            question.wrongAnswer1,
            question.wrongAnswer2,
            question.wrongAnswer3
        ).shuffled()
    }

    fun clearAnswerResult() {
        _answerResult.value = null
    }

    data class AnswerResult(
        val isCorrect: Boolean,
        val correctAnswer: String
    )
}