package com.example.codequizmaster.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.codequizmaster.data.entity.GameSession

@Dao
interface GameSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(gameSession: GameSession): Long

    @Query("SELECT * FROM game_sessions WHERE userId = :userId ORDER BY playedAt DESC")
    fun getSessionsByUser(userId: Long): LiveData<List<GameSession>>

    @Query("SELECT * FROM game_sessions WHERE userId = :userId ORDER BY playedAt DESC LIMIT :limit")
    suspend fun getRecentSessions(userId: Long, limit: Int): List<GameSession>

    @Query("SELECT MAX(score) FROM game_sessions WHERE userId = :userId")
    suspend fun getBestScore(userId: Long): Int?

    @Query("SELECT AVG(score) FROM game_sessions WHERE userId = :userId")
    suspend fun getAverageScore(userId: Long): Float?

    @Query("SELECT COUNT(*) FROM game_sessions WHERE userId = :userId")
    suspend fun getTotalGamesPlayed(userId: Long): Int

    @Query("DELETE FROM game_sessions")
    suspend fun deleteAll()

    @Query("DELETE FROM game_sessions WHERE userId = :userId")
    suspend fun deleteUserSessions(userId: Long)
}