package com.hardik.remember.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hardik.remember.models.BlogResponseItem

@Dao
interface DaoContent {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(content: BlogResponseItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(content: List<BlogResponseItem>): List<Long>

    @Query("SELECT * FROM blog WHERE title = :title ORDER BY title ASC")//DESC
    fun getContent(title: String): LiveData<BlogResponseItem>

    @Query("SELECT * FROM blog WHERE id = :id ORDER BY title ASC")//DESC
    fun getContent(id: Int): LiveData<BlogResponseItem>

    @Query("SELECT * FROM blog ORDER BY title ASC")
    fun getAllContents(): LiveData<List<BlogResponseItem>>

    @Query("SELECT * FROM blog WHERE is_like = :isLike ORDER BY title ASC")//DESC
    fun getAllContents(isLike: Boolean): LiveData<List<BlogResponseItem>>

    @Query("SELECT is_like FROM blog WHERE id = :id")
    fun isLike(id: Int): LiveData<Boolean>

    @Query("SELECT is_like FROM blog WHERE LOWER(title) = LOWER(:title) LIMIT 1")
    fun isLike(title: String): LiveData<Boolean>

    @Query("SELECT EXISTS (SELECT 1 FROM blog WHERE title = :title LIMIT 1)")
    fun containsContent(title: String): LiveData<Boolean>

    @Query("SELECT EXISTS (SELECT 1 FROM blog WHERE LOWER(title) = LOWER(:title) LIMIT 1)")
    fun containsContentsIgnoreCase(title: String): LiveData<Boolean>

    @Delete
    suspend fun delete(content: BlogResponseItem)

    @Query("DELETE FROM blog WHERE title = :title")
    suspend fun delete(title: String)
}