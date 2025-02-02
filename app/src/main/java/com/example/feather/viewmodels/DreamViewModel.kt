package com.example.feather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.example.feather.service.DreamService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DreamViewModel @Inject constructor(private val dreamService: DreamService) : ViewModel(){

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _saveKeywordResult = MutableLiveData<Result<Unit>>()
    val saveKeywordResult: LiveData<Result<Unit>> = _saveKeywordResult

    private val _userKeywords = MutableLiveData<List<KeywordModel>>()
    val userKeywords: LiveData<List<KeywordModel>> = _userKeywords


    fun saveDream(dream: DreamModel) {
        viewModelScope.launch {
            _saveResult.value = runCatching { dreamService.saveDream(dream) }
        }
    }

    fun saveKeyword(keyword: KeywordModel){
        viewModelScope.launch {
            _saveKeywordResult.value = runCatching { dreamService.saveKeyword(keyword) }
        }
    }

    fun getUserKeywords() {
        Log.d("keyword", "entered getuserkeywords vm")
        viewModelScope.launch {
            val keywords = dreamService.getUserKeywords()
            Log.d("keyword vm2", keywords.toString())
            _userKeywords.value = keywords
        }
    }



}