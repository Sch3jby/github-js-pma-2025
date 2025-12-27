package com.example.codequizmaster.data.repository

import androidx.lifecycle.LiveData
import com.example.codequizmaster.data.dao.GameSessionDao
import com.example.codequizmaster.data.dao.QuestionDao
import com.example.codequizmaster.data.dao.UserDao
import com.example.codequizmaster.data.entity.GameSession
import com.example.codequizmaster.data.entity.Question
import com.example.codequizmaster.data.entity.User

class QuizRepository(
    private val questionDao: QuestionDao,
    private val userDao: UserDao,
    private val gameSessionDao: GameSessionDao
) {

    // === QUESTION OPERATIONS ===

    fun getAllQuestions(): LiveData<List<Question>> = questionDao.getAllQuestions()

    suspend fun getRandomQuestions(difficulty: String, count: Int): List<Question> {
        return questionDao.getRandomQuestionsByDifficulty(difficulty, count)
    }

    suspend fun getQuestionsCount(): Int = questionDao.getQuestionsCount()

    suspend fun getQuestionsCountByDifficulty(difficulty: String): Int {
        return questionDao.getQuestionsCountByDifficulty(difficulty)
    }

    suspend fun insertQuestion(question: Question) = questionDao.insert(question)

    suspend fun insertQuestions(questions: List<Question>) = questionDao.insertAll(questions)

    // === USER OPERATIONS ===

    suspend fun createUser(username: String): Long {
        val user = User(username = username)
        return userDao.insert(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }

    fun getUserByIdLiveData(userId: Long): LiveData<User?> {
        return userDao.getUserByIdLiveData(userId)
    }

    suspend fun updateUser(user: User) = userDao.update(user)

    suspend fun updateUserStatsAfterGame(userId: Long, score: Int) {
        userDao.updateUserStats(userId, score, System.currentTimeMillis())
    }

    suspend fun usernameExists(username: String): Boolean {
        return userDao.usernameExists(username) > 0
    }

    fun getAllUsers(): LiveData<List<User>> = userDao.getAllUsers()

    // === GAME SESSION OPERATIONS ===

    suspend fun saveGameSession(gameSession: GameSession): Long {
        return gameSessionDao.insert(gameSession)
    }

    fun getUserSessions(userId: Long): LiveData<List<GameSession>> {
        return gameSessionDao.getSessionsByUser(userId)
    }

    suspend fun getRecentSessions(userId: Long, limit: Int): List<GameSession> {
        return gameSessionDao.getRecentSessions(userId, limit)
    }

    suspend fun getBestScore(userId: Long): Int {
        return gameSessionDao.getBestScore(userId) ?: 0
    }

    suspend fun getAverageScore(userId: Long): Float {
        return gameSessionDao.getAverageScore(userId) ?: 0f
    }

    suspend fun getTotalGamesPlayed(userId: Long): Int {
        return gameSessionDao.getTotalGamesPlayed(userId)
    }
}