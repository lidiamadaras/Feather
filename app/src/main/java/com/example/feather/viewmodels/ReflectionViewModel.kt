package com.example.feather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.ReflectionModel
import com.example.feather.service.ReflectionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReflectionViewModel @Inject constructor(private val reflectionService: ReflectionService) : ViewModel(){

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _userReflections = MutableLiveData<List<ReflectionModel>>()
    val userReflections: LiveData<List<ReflectionModel>> = _userReflections

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult


    fun saveReflection(reflection: ReflectionModel) {
        viewModelScope.launch {
            _saveResult.value = runCatching { reflectionService.saveReflection(reflection) }
        }
    }

    fun getUserReflections() {
        viewModelScope.launch {
            val reflections = reflectionService.getUserReflections()
            _userReflections.value = reflections
        }
    }

    fun deleteReflection(reflectionId: String) {
        viewModelScope.launch {
            _deleteResult.value = runCatching { reflectionService.deleteReflection(reflectionId) }
        }
    }
}