package com.example.codequizmaster.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.codequizmaster.data.entity.Question

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): LiveData<List<Question>>

    @Query("SELECT * FROM questions WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit")
    suspend fun getRandomQuestionsByDifficulty(difficulty: String, limit: Int): List<Question>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionsCount(): Int

    @Query("SELECT COUNT(*) FROM questions WHERE difficulty = :difficulty")
    suspend fun getQuestionsCountByDifficulty(difficulty: String): Int

    @Query("DELETE FROM questions")
    suspend fun deleteAll()

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getQuestionById(id: Long): Question?
}