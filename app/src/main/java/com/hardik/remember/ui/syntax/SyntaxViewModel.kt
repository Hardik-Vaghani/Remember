package com.hardik.remember.ui.syntax

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hardik.remember.R
import com.hardik.remember.models.SyntaxResponseItem
import com.hardik.remember.util.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream

class SyntaxViewModel(app: Application) : ViewModel() {

    // Assuming 'syntax.json' is the name of your JSON file in the 'raw' directory
    private val inputStream: InputStream = app.resources.openRawResource(R.raw.syntax)

    private val jsonParser = JsonParser()

    // LiveData to observe changes in the data
    private val _sentenceDataList = MutableLiveData<List<SyntaxResponseItem>>()
    val sentenceDataList: LiveData<List<SyntaxResponseItem>> get() = _sentenceDataList

    init {
        // Load data in the ViewModel's init block or in a function
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Parse the JSON file and set the data in _sentenceDataList
                _sentenceDataList.postValue(jsonParser.parseJson(inputStream))
            } catch (e: IOException) {
                // Handle exception appropriately (e.g., log or show an error message)
                e.printStackTrace()
            }
        }
    }

    fun deleteSpelling(syntaxResponseItem: SyntaxResponseItem?) {
        syntaxResponseItem?.let { itemToDelete ->
            val currentList = _sentenceDataList.value.orEmpty().toMutableList()

            // Remove the item from the list
            currentList.remove(itemToDelete)

            // Update the LiveData with the modified list
            _sentenceDataList.value = currentList
        }
    }

    fun saveSpelling(syntaxResponseItem: SyntaxResponseItem?) {
        syntaxResponseItem?.let { newItem ->
            val currentList = _sentenceDataList.value.orEmpty().toMutableList()

            // Add the new item to the list
            currentList.add(newItem)

            // Update the LiveData with the modified list
            _sentenceDataList.postValue(currentList)
        }
    }

//    private val _text = MutableLiveData<String>().apply {
//        value = "This is slideshow Fragment"
//        val htmlString = "This is <b>bold</b> and this is <i>italic</i>. " +
//                "This is <u>underlined</u> text. " +
//                "This is <font color=\"red\">red</font> and this is <font size=\"20\">larger</font> text. " +
//                "This is a line break:<br>Now on a new line. " +
//                "This is <small>smaller</small> text and this is <big>larger</big> text. " +
//                "This is <sup>superscript</sup> and this is <sub>subscript</sub> text. " +
//                "This is <s>strikethrough</s> and this is <strong>strong</strong> text. " +
//                "This is <em>emphasized</em> text. " +
//                "<span style=\"color: blue;\">This is blue text using inline styling.</span> " +
//                "<div style=\"background-color: #FFCC00;\">This is a block-level container with a background color.</div> " +
//                "This is a <a href=\"https://example.com\">hyperlink</a>."
//
//        value = htmlString
//    }
//    val text: LiveData<String> = _text

    // access in fragment onCreateView()
//        slideshowViewModel = ViewModelProvider(this).get(SlideshowViewModel::class.java)
//        val textView: TextView = binding.textSlideshow
//        slideshowViewModel.text.observe(viewLifecycleOwner) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                textView.text = Html.fromHtml(it, Html.FROM_HTML_MODE_LEGACY)
//            } else {
//                @Suppress("DEPRECATION")
//                textView.text = HtmlCompat.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
//            }
//            textView.text = it
//        }

}