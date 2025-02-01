package com.example.feather.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.DreamModel
import com.example.feather.service.DreamService
import kotlinx.coroutines.launch

class DreamViewModel(private val dreamService: DreamService) : ViewModel(){

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    fun saveDream(dream: DreamModel) {
        viewModelScope.launch {
            _saveResult.value = runCatching { dreamService.saveDream(dream) }
        }
    }

}