package com.hardik.remember.repository

import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.SpellingResponseItem

class SpellingRepositoryInstance(private val db: DBInstance) {

    suspend fun upsert(spelling: SpellingResponseItem) = db.getDBDaoSpelling().upsert(spelling)
    suspend fun upsert(spelling: List<SpellingResponseItem>) = db.getDBDaoSpelling().upsert(spelling)
    fun getSpelling(word: String) = db.getDBDaoSpelling().getSpelling(word)
    fun getAllSpellings() = db.getDBDaoSpelling().getAllSpellings()
    fun getAllSpellings(isLike: Boolean) = db.getDBDaoSpelling().getAllSpellings(isLike)
    fun isLike(wordId: Int) = db.getDBDaoSpelling().isLike(wordId)
    fun isLike(word: String) = db.getDBDaoSpelling().isLike(word)
    fun containsSpellings(word: String) = db.getDBDaoSpelling().containsSpelling(word)
    fun containsSpellingsIgnoreCase(word: String) = db.getDBDaoSpelling().containsSpellingsIgnoreCase(word)
    suspend fun delete(spelling: SpellingResponseItem) = db.getDBDaoSpelling().delete(spelling)
    suspend fun delete(word: String) = db.getDBDaoSpelling().delete(word)
}