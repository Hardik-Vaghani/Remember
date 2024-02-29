package com.hardik.remember.ui.spelling_widget

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hardik.remember.repository.SpellingRepositoryInstance

class SpellingAppWidgetViewModelProviderFactory(private val app: Application, private val spellingRepositoryInstance: SpellingRepositoryInstance): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SpellingAppWidgetViewModel(app,spellingRepositoryInstance) as T
    }
}