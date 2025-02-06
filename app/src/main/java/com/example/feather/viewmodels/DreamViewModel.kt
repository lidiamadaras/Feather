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

    private val _userDreams = MutableLiveData<List<DreamModel>>()
    val userDreams: LiveData<List<DreamModel>> = _userDreams

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult


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
        viewModelScope.launch {
            val keywords = dreamService.getUserKeywords()
            _userKeywords.value = keywords
        }
    }

    fun getUserDreams() {
        viewModelScope.launch {
            val dreams = dreamService.getUserDreams()
            _userDreams.value = dreams
        }
    }

    fun deleteDream(dreamId: String) {
        viewModelScope.launch {
            _deleteResult.value = runCatching { dreamService.deleteDream(dreamId) }
        }
    }



}