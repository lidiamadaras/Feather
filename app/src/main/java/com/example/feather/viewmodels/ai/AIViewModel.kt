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
import com.example.feather.models.DreamInterpretationModel

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

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _preferredPersona = MutableLiveData<Result<String?>>()
    val preferredPersona: LiveData<Result<String?>> = _preferredPersona

    private val _imageResult = MutableLiveData<String?>()
    val imageResult: LiveData<String?> get() = _imageResult

    private val _userInterpretations = MutableLiveData<List<DreamInterpretationModel>>()
    val userInterpretations: LiveData<List<DreamInterpretationModel>> = _userInterpretations

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _interpretation = MutableLiveData<DreamInterpretationModel?>()
    val interpretation: LiveData<DreamInterpretationModel?> = _interpretation


//    fun generateImage(prompt: String) {
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                val imageData = aiService.generateImage(prompt)
//                val savedPath = imageData?.let { aiService.saveImage(it) }
//                _imageResult.value = savedPath
//            } catch (e: Exception) {
//                Log.e("AIViewModel", "Image generation failed", e)
//                _imageResult.value = null
//            }
//            _isLoading.postValue(false)
//        }
//    }

//    fun generateImage(prompt: String) {
//        _isLoading.value = true
//        viewModelScope.launch {
//            try {
//                val imageData = aiService.generateImage(prompt)
//                val savedPath = imageData?.let { aiService.saveImage(it) }
//                _imageResult.value = savedPath
//            } catch (e: Exception) {
//                Log.e("AIViewModel", "Image generation failed", e)
//                _imageResult.value = null
//            }
//            _isLoading.postValue(false)
//        }
//    }

    fun savePreferredPersona(persona: String) {
        Log.d("Persona VM", "entered savepersona in vm")
        viewModelScope.launch {
            try {
                aiService.savePreferredPersona(persona)
                Log.d("Persona VM", persona)
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

    fun analyzeDream(dream: DreamModel, prompt: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = aiService.analyzeDream(dream, prompt)
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

    fun analyzeWeeklyDreams(prompt: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = aiService.analyzeWeekly(prompt)
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

    fun analyzeMonthlyDreams(prompt: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = aiService.analyzeMonthly(prompt)
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

    fun saveAnalysis(analysisText: String, type: String, persona: String, title: String) {
        viewModelScope.launch {
            _saveResult.value = runCatching { aiService.saveAnalysis(analysisText, type, persona, title) }
        }
    }

    fun getUserInterpretations(type: String) {
        viewModelScope.launch {
            val interpretations = aiService.getUserInterpretations(type)
            _userInterpretations.value = interpretations
        }
    }

    fun deleteInterpretation(id: String, type: String) {
        viewModelScope.launch {
            _deleteResult.value = runCatching { aiService.deleteInterpretation(id, type) }
        }
    }

    fun getInterpretationById(id: String, type: String) {
        viewModelScope.launch {
            _interpretation.value = aiService.getInterpretationById(id, type)
        }
    }
}
