package com.example.feather.viewmodels.ai

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.DreamModel
import com.example.feather.service.ai.AIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.graphics.Bitmap
import android.graphics.BitmapFactory

@HiltViewModel
class AIViewModel @Inject constructor(
    private val aiService: AIService
) : ViewModel() {

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _analysisResult = MutableLiveData<String?>()
    val analysisResult: LiveData<String?> get() = _analysisResult

    private val _analysisResultWeekly = MutableLiveData<String?>()
    val analysisResultWeekly: LiveData<String?> get() = _analysisResultWeekly

    private val _analysisResultMonthly = MutableLiveData<String?>()
    val analysisResultMonthly: LiveData<String?> get() = _analysisResultMonthly

    private val _imageResult = MutableLiveData<Bitmap?>()
    val imageResult: LiveData<Bitmap?> get() = _imageResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _preferredPersona = MutableLiveData<Result<String?>>()
    val preferredPersona: LiveData<Result<String?>> = _preferredPersona

    fun savePreferredPersona(persona: String) {
        viewModelScope.launch {
            try {
                aiService.savePreferredPersona(persona)
                _preferredPersona.value = Result.success(persona)
            } catch (e: Exception) {
                _preferredPersona.value = Result.failure(e)
            }
        }
    }

    fun loadPreferredPersona() {
        viewModelScope.launch {
            try {
                val persona = aiService.loadPreferredPersona()
                _preferredPersona.postValue(Result.success(persona))
            } catch (e: Exception) {
                _preferredPersona.postValue(Result.failure(e))
            }
        }
    }

    fun generateImage(dream: DreamModel) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                //val result = aiService.generateImage(dream)
                //_imageResult.value = result
//                result.onSuccess { imageUrl ->
//                    _imageResult.value = imageUrl // Now we have a URL, not raw image bytes
//                }
//                result.onFailure { error ->
//                    Log.e("AIViewModel", "Dream image generation failed: ${error.localizedMessage}", error)
//                    _imageResult.value = null
//                }
            } catch (e: Exception) {
                Log.e("AIViewModel", "Unexpected error", e)
                _imageResult.value = null
            }
            _isLoading.postValue(false)
        }
    }

    fun analyzeDream(dream: DreamModel) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = aiService.analyzeDream(dream)
                result.onSuccess { response ->
                    _analysisResult.value = response
                }
                result.onFailure { error ->
                    val errorMessage = error.localizedMessage ?: "Unknown error occurred"
                    Log.e("AIViewModel", "Dream analysis failed: $errorMessage", error)
                    _analysisResult.value = "Analysis failed: $errorMessage"
                }
            } catch (e: Exception) {
                Log.e("AIViewModel", "Unexpected error", e)
                _analysisResult.value = "Analysis failed: ${e.localizedMessage ?: "Unexpected error"}"
            }
            _isLoading.postValue(false)
        }
    }

    fun analyzeWeeklyDreams() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = aiService.analyzeWeekly()
                result.onSuccess { response ->
                    _analysisResultWeekly.value = response
                }
                result.onFailure { error ->
                    val errorMessage = error.localizedMessage ?: "Unknown error occurred"
                    Log.e("AIViewModel", "Dream analysis failed: $errorMessage", error)
                    _analysisResultWeekly.value = "Analysis failed: $errorMessage"
                }
            } catch (e: Exception) {
                Log.e("AIViewModel", "Unexpected error", e)
                _analysisResultWeekly.value = "Analysis failed: ${e.localizedMessage ?: "Unexpected error"}"
            }
            _isLoading.postValue(false)
        }
    }

    fun analyzeMonthlyDreams() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = aiService.analyzeMonthly()
                result.onSuccess { response ->
                    _analysisResultMonthly.value = response
                }
                result.onFailure { error ->
                    val errorMessage = error.localizedMessage ?: "Unknown error occurred"
                    Log.e("AIViewModel", "Dream analysis failed: $errorMessage", error)
                    _analysisResultMonthly.value = "Analysis failed: $errorMessage"
                }
            } catch (e: Exception) {
                Log.e("AIViewModel", "Unexpected error", e)
                _analysisResultMonthly.value = "Analysis failed: ${e.localizedMessage ?: "Unexpected error"}"
            }
            _isLoading.postValue(false)
        }
    }

    fun saveAnalysis(analysisText: String, type: String) {
        viewModelScope.launch {
            _saveResult.value = runCatching { aiService.saveAnalysis(analysisText, type) }
        }
    }
}
