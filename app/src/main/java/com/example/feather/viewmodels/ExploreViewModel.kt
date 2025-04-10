package com.example.feather.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.AffirmationModel
import com.example.feather.models.SymbolModel
import com.example.feather.service.ExploreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(private val exploreService: ExploreService) : ViewModel(){

    private val _symbols = MutableLiveData<List<SymbolModel>>()
    val symbols: LiveData<List<SymbolModel>> = _symbols

    private var allSymbols = listOf<SymbolModel>()

    private val _symbol = MutableLiveData<SymbolModel?>()
    val symbol: LiveData<SymbolModel?> = _symbol

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _answerResult = MutableLiveData<String?>()
    val answerResult: LiveData<String?> get() = _answerResult


    fun getSymbolById(id: String) {
        viewModelScope.launch {
            _symbol.value = exploreService.getSymbolById(id)
        }
    }

    fun getSymbols() {
        _isLoading.value = true
        viewModelScope.launch {
            val temp = exploreService.getSymbols()
            allSymbols = temp
            _symbols.value = temp
        }
        _isLoading.value = false
    }

    fun askAboutSymbol(symbol: String, prompt: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = exploreService.askAboutSymbol(symbol, prompt)
                result.onSuccess { response ->
                    _answerResult.value = response
                }
                result.onFailure { error ->
                    val errorMessage = error.localizedMessage ?: "Unknown error occurred"
                    Log.e("ExploreViewModel", "Failed to answer: $errorMessage", error)
                    _answerResult.value = "Answering failed: $errorMessage"
                }
            } catch (e: Exception) {
                Log.e("ExploreViewModel", "Unexpected error", e)
                _answerResult.value = "Answering failed: ${e.localizedMessage ?: "Unexpected error"}"
            }
            _isLoading.postValue(false)
        }
    }

    fun filterSymbols(query: String) {
        val filteredList = if (query.isEmpty()) {
            allSymbols
        } else {
            val matches = allSymbols.filter { it.name.contains(query, ignoreCase = true) }

            matches.ifEmpty {
                // Add the "no match" symbol if no matches are found
                listOf(SymbolModel(id = "no_match", name = ""))
            }
        }

        _symbols.value = filteredList
    }

//    fun loadCSVSymbols(context: Context) {
//        Log.d("Firestore", "entered VM function")
//        exploreService.loadCSVSymbols(context)
//    }

}