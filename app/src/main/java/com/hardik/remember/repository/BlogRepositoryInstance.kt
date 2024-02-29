package com.hardik.remember.repository

import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.BlogResponseItem

class BlogRepositoryInstance(private val db: DBInstance) {

    suspend fun upsert(content: BlogResponseItem) = db.getDBDaoContent().upsert(content)
    suspend fun upsert(content: List<BlogResponseItem>) = db.getDBDaoContent().upsert(content)
    fun getBlog(title:String) = db.getDBDaoContent().getContent(title)
    fun getBlog(id: Int) = db.getDBDaoContent().getContent(id)
    fun getAllBlogs() = db.getDBDaoContent().getAllContents()
    fun getAllBlogs(isLike:Boolean) = db.getDBDaoContent().getAllContents(isLike)
    fun getContents(isLike:Boolean) = db.getDBDaoContent().getAllContents(isLike)
    fun isLike(contentId: Int) = db.getDBDaoContent().isLike(contentId)
    fun isLike(contentTitle: String) = db.getDBDaoContent().isLike(contentTitle)
    fun containsContent(title: String) = db.getDBDaoContent().containsContent(title)
    fun containsBlogsIgnoreCase(title: String) = db.getDBDaoContent().containsContentsIgnoreCase(title)
    suspend fun delete(content: BlogResponseItem) = db.getDBDaoContent().delete(content)
    suspend fun delete(title: String) = db.getDBDaoContent().delete(title)
}