package com.hardik.remember.ui.blog

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hardik.remember.repository.BlogRepositoryInstance

class BlogViewModelProviderFactory(private val app: Application, private val blogRepositoryInstance: BlogRepositoryInstance): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BlogViewModel(app,blogRepositoryInstance) as T
    }
}