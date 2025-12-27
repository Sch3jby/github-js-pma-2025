package com.example.codequizmaster.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.codequizmaster.data.database.QuizDatabase
import com.example.codequizmaster.data.entity.GameSession
import com.example.codequizmaster.data.entity.User
import com.example.codequizmaster.data.repository.QuizRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: QuizRepository

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _userSessions = MutableLiveData<List<GameSession>>()
    val userSessions: LiveData<List<GameSession>> = _userSessions

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    init {
        val database = QuizDatabase.getDatabase(application)
        repository = QuizRepository(
            database.questionDao(),
            database.userDao(),
            database.gameSessionDao()
        )
    }

    fun loadUser(userId: Long) {
        viewModelScope.launch {
            val user = repository.getUserById(userId)
            _currentUser.value = user

            if (user != null) {
                loadUserStats(userId)
            }
        }
    }

    private suspend fun loadUserStats(userId: Long) {
        val bestScore = repository.getBestScore(userId)
        val avgScore = repository.getAverageScore(userId)
        val totalGames = repository.getTotalGamesPlayed(userId)
        val recentSessions = repository.getRecentSessions(userId, 5)

        _userStats.postValue(
            UserStats(
                bestScore = bestScore,
                averageScore = avgScore,
                totalGames = totalGames
            )
        )

        _userSessions.postValue(recentSessions)
    }

    fun observeUser(userId: Long): LiveData<User?> {
        return repository.getUserByIdLiveData(userId)
    }

    fun observeUserSessions(userId: Long): LiveData<List<GameSession>> {
        return repository.getUserSessions(userId)
    }

    data class UserStats(
        val bestScore: Int,
        val averageScore: Float,
        val totalGames: Int
    )
}