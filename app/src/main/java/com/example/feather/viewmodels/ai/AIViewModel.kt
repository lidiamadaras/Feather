package com.example.feather.viewmodels.ai

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.AffirmationModel
import com.example.feather.models.DreamModel
import com.example.feather.service.ai.AIService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    fun analyzeDream(dream: DreamModel) {
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
        }
    }

    fun analyzeWeeklyDreams() {
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
        }
    }

    fun analyzeMonthlyDreams() {
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
        }
    }

    fun saveAnalysis(analysisText: String, type: String) {
        viewModelScope.launch {
            _saveResult.value = runCatching { aiService.saveAnalysis(analysisText, type) }
        }
    }
}
