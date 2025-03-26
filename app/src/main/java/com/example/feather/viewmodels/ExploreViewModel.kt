package com.example.feather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun getSymbols() {
        viewModelScope.launch {
            val temp = exploreService.getSymbols()
            allSymbols = temp 
            _symbols.value = temp
        }
    }

    fun filterSymbols(query: String) {
        val filteredList = if (query.isEmpty()) {
            allSymbols
        } else {
            allSymbols.filter { it.name.contains(query, ignoreCase = true) }
        }
        _symbols.value = filteredList
    }
}