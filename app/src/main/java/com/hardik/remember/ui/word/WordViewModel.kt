package com.hardik.remember.ui.word

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hardik.remember.models.SpellingResponseItem
import com.hardik.remember.repository.SpellingRepositoryInstance
import com.hardik.remember.util.Resource
import kotlinx.coroutines.launch

class WordViewModel(app: Application, private val spellingRepositoryInstance: SpellingRepositoryInstance) : AndroidViewModel(app) {

    val spelling : MutableLiveData<Resource<ArrayList<SpellingResponseItem>>> = MutableLiveData()

    fun saveSpelling(spelling: SpellingResponseItem) = viewModelScope.launch { spellingRepositoryInstance.upsert(spelling) }
    fun saveSpelling(spelling: List<SpellingResponseItem>) = viewModelScope.launch { spellingRepositoryInstance.upsert(spelling) }
    fun getSpelling(word: String) = spellingRepositoryInstance.getSpelling(word)
    fun getAllSpelling() = spellingRepositoryInstance.getAllSpellings()
    fun getAllSpelling(isLike: Boolean) = spellingRepositoryInstance.getAllSpellings(isLike)
    fun isLike(wordId: Int) = spellingRepositoryInstance.isLike(wordId)
    fun isLike(word: String) = spellingRepositoryInstance.isLike(word)
    fun containsSpellings(word: String) = spellingRepositoryInstance.containsSpellings(word)
    fun containsSpellingsIgnoreCase(word: String) = spellingRepositoryInstance.containsSpellingsIgnoreCase(word)
    fun deleteSpelling(spelling: SpellingResponseItem) = viewModelScope.launch { spellingRepositoryInstance.delete(spelling)}
    fun deleteSpelling(word: String) = viewModelScope.launch { spellingRepositoryInstance.delete(word)}

    override fun onCleared() {
        super.onCleared()
        Log.e("TAG", "onCleared: ", )
    }
}