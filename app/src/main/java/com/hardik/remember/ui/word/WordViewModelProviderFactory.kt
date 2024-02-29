package com.hardik.remember.ui.word

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hardik.remember.repository.SpellingRepositoryInstance

class WordViewModelProviderFactory(private val app: Application, private val spellingRepositoryInstance: SpellingRepositoryInstance): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WordViewModel(app,spellingRepositoryInstance) as T
    }
}