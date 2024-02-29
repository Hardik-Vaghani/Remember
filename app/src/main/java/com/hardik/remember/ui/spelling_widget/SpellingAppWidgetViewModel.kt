package com.hardik.remember.ui.spelling_widget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardik.remember.db.DBInstance
import com.hardik.remember.models.SpellingResponseItem
import com.hardik.remember.repository.SpellingRepositoryInstance
import com.hardik.remember.util.DataCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//class SpellingAppWidgetViewModel(application: Application, private val spellingRepositoryInstance: SpellingRepositoryInstance) : ViewModel() {
class SpellingAppWidgetViewModel(application: Application, private val spellingRepositoryInstance: SpellingRepositoryInstance) : AndroidViewModel(application) {

//    val allSpellingItems: LiveData<List<SpellingResponseItem>> = spellingRepositoryInstance.getAllSpellings()
    private val _spellingDataList = MutableLiveData<List<SpellingResponseItem>>()
    val spellingDataList: LiveData<List<SpellingResponseItem>> get() = _spellingDataList

    init {
        getSpellingData()
    }

    private fun getSpellingData() {
        viewModelScope.launch(Dispatchers.IO) {
            _spellingDataList.value = spellingRepositoryInstance.getAllSpellings(isLike = true).value
        }
    }

    fun deleteSpelling(spellingResponseItem: SpellingResponseItem){
        spellingResponseItem.let { itemToDelete ->
            val currentList = _spellingDataList.value.orEmpty().toMutableList()

            currentList.remove(itemToDelete)

            _spellingDataList.value = currentList
        }
    }

    fun saveSpelling(spellingResponseItem: SpellingResponseItem){
        spellingResponseItem?.let { itemToSave ->
            val currentList = _spellingDataList.value.orEmpty().toMutableList()

            currentList.add(itemToSave)

            _spellingDataList.value = currentList
        }
    }
}
