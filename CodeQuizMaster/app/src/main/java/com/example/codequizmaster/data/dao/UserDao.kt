package com.example.codequizmaster.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.codequizmaster.data.entity.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Long): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdLiveData(userId: Long): LiveData<User?>

    @Query("SELECT * FROM users ORDER BY totalScore DESC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("""
        UPDATE users 
        SET totalGamesPlayed = totalGamesPlayed + 1,
            totalScore = totalScore + :score,
            highestScore = CASE 
                WHEN :score > highestScore THEN :score 
                ELSE highestScore 
            END,
            lastPlayed = :timestamp
        WHERE id = :userId
    """)
    suspend fun updateUserStats(userId: Long, score: Int, timestamp: Long)

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun usernameExists(username: String): Int
}