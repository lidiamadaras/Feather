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
}