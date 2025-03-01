package com.example.feather.viewmodels.ai

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.feather.service.ai.ApiKeyService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ApiKeyViewModel @Inject constructor(private val service: ApiKeyService): ViewModel(){
    private val _apiKey = MutableLiveData<String?>()
    val apiKey: LiveData<String?> get() = _apiKey

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun loadApiKey() {
        _apiKey.value = service.getApiKey()
    }

    fun saveApiKey(key: String) {
        val result = service.saveApiKey(key)
        result.onSuccess {
            _apiKey.value = key
        }.onFailure { exception ->
            _errorMessage.value = exception.message
        }
    }

    fun clearApiKey() {
        service.clearApiKey()
        _apiKey.value = null
    }
}