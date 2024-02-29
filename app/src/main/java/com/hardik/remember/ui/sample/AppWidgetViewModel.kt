package com.hardik.remember.ui.sample

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.SpellingResponseItem
import com.hardik.remember.repository.SpellingRepositoryInstance
import com.hardik.remember.util.DataCache

class AppWidgetViewModel(application: Application) : AndroidViewModel(application) {
    private val repositoryInstance: SpellingRepositoryInstance = SpellingRepositoryInstance(DBInstance(application))

    val allSpellingItems: LiveData<List<SpellingResponseItem>> = repositoryInstance.getAllSpellings()

    private val _data = MutableLiveData<List<SpellingResponseItem>>()
    val data: LiveData<List<SpellingResponseItem>> get() = _data

    fun setData(newData: List<SpellingResponseItem>) {
        _data.value = newData
        // Cache the data for persistence
        DataCache.cachedData = newData
    }
}
