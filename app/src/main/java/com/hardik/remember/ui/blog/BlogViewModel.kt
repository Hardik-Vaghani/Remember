package com.hardik.remember.ui.blog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hardik.remember.models.BlogResponseItem
import com.hardik.remember.repository.BlogRepositoryInstance
import kotlinx.coroutines.launch

class BlogViewModel (app: Application, private val blogRepositoryInstance: BlogRepositoryInstance) : AndroidViewModel(app) {

    fun saveBlog(content: BlogResponseItem) = viewModelScope.launch { blogRepositoryInstance.upsert(content) }
    fun saveBlogs(content: List<BlogResponseItem>) = viewModelScope.launch { blogRepositoryInstance.upsert(content) }
    fun getBlog(title: String) = blogRepositoryInstance.getBlog(title)
    fun getBlog(id :Int) = blogRepositoryInstance.getBlog(id)
    fun getAllBlog() = blogRepositoryInstance.getAllBlogs()
    fun getAllBlog(isLike: Boolean) = blogRepositoryInstance.getAllBlogs(isLike)
    fun isLike(contentId: Int) = blogRepositoryInstance.isLike(contentId)
    fun isLike(contentTitle: String) = blogRepositoryInstance.isLike(contentTitle)
    fun containsBlogs(contentTitle: String) = blogRepositoryInstance.containsContent(contentTitle)
    fun containsBlogsIgnoreCase(contentTitle: String) = blogRepositoryInstance.containsBlogsIgnoreCase(contentTitle)
    fun deleteBlog(content: BlogResponseItem) = viewModelScope.launch { blogRepositoryInstance.delete(content)}
    fun deleteBlog(title: String) = viewModelScope.launch { blogRepositoryInstance.delete(title)}
}