package com.hardik.remember.ui.syntax

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SyntaxViewModelProviderFactory(private val app: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SyntaxViewModel(app) as T
    }
}