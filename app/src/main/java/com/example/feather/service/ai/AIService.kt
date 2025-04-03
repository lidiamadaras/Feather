package com.example.feather.service.ai

import com.example.feather.ai.SecureStorage
import com.example.feather.models.DreamModel
import com.example.feather.repository.ai.AIRepository
import javax.inject.Inject

class AIService @Inject constructor(
    private val repository: AIRepository,
    private val safeStorage: SecureStorage
) {
    suspend fun analyzeDream(dream: DreamModel): Result<String> {
        val apiKey = safeStorage.getApiKey() ?: return Result.failure(Exception("API key missing"))
        return repository.analyzeDream(apiKey, dream)
    }

    suspend fun analyzeWeekly(): Result<String> {
        val apiKey = safeStorage.getApiKey() ?: return Result.failure(Exception("API key missing"))
        return repository.weeklyAnalysis(apiKey)
    }

    suspend fun analyzeMonthly(): Result<String> {
        val apiKey = safeStorage.getApiKey() ?: return Result.failure(Exception("API key missing"))
        return repository.monthlyAnalysis(apiKey)
    }

    suspend fun saveAnalysis(analysisText: String, type: String): Result<Unit> {
        return repository.saveInterpretation(analysisText, type)
    }

    suspend fun savePreferredPersona(persona: String) {
        return repository.savePreferredPersona(persona)
    }

    suspend fun loadPreferredPersona(): String? {
        return repository.loadPreferredPersona()
    }

//    suspend fun generateImage(prompt: String): String?{
//        val apiKey = safeStorage.getApiKey() ?: return null
//        return repository.generateImage(apiKey, prompt)
//    }

    fun generateImage(prompt: String): Unit? {
        val apiKey = safeStorage.getApiKey()
        return apiKey?.let { repository.generateImage(it, prompt) }
    }

    fun saveImage(imageData: String): String? {
        return repository.saveImage(imageData)
    }


//    suspend fun generateImage(dream: DreamModel): Bitmap? {
//        val apiKey = safeStorage.getApiKey() ?: return null
//        return repository.generateImageOfDream(apiKey, dream)
//    }

}