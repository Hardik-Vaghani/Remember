package com.hardik.remember.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hardik.remember.models.SpellingResponseItem
@Dao
interface DaoSpelling {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(spelling: SpellingResponseItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(spelling: List<SpellingResponseItem>): List<Long>

    @Query("SELECT * FROM spelling WHERE word = :word ORDER BY word ASC")//DESC
    fun getSpelling(word: String): LiveData<SpellingResponseItem>

    @Query("SELECT * FROM spelling ORDER BY word ASC")//DESC
    fun getAllSpellings(): LiveData<List<SpellingResponseItem>>

    @Query("SELECT * FROM spelling WHERE is_like = :isLike ORDER BY word ASC")//DESC
    fun getAllSpellings(isLike: Boolean): LiveData<List<SpellingResponseItem>>

    @Query("SELECT is_like FROM spelling WHERE id = :wordId")
    fun isLike(wordId: Int): LiveData<Boolean>

    @Query("SELECT is_like FROM spelling WHERE LOWER(word) = LOWER(:word) LIMIT 1")
    fun isLike(word: String): LiveData<Boolean>

    @Query("SELECT EXISTS (SELECT 1 FROM spelling WHERE word = :word LIMIT 1)")
    fun containsSpelling(word: String): LiveData<Boolean>

    @Query("SELECT EXISTS (SELECT 1 FROM spelling WHERE LOWER(word) = LOWER(:word) LIMIT 1)")
    fun containsSpellingsIgnoreCase(word: String): LiveData<Boolean>

    @Delete
    suspend fun delete(spelling: SpellingResponseItem)

    @Query("DELETE FROM spelling WHERE word = :word")
    suspend fun delete(word: String)
}