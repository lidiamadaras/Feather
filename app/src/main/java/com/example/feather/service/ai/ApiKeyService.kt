package com.example.feather.service.ai

import com.example.feather.repository.ai.ApiKeyRepository
import javax.inject.Inject

class ApiKeyService @Inject constructor(private val repository: ApiKeyRepository){
    fun saveApiKey(apiKey: String): Result<Unit> {
        return if (isValidApiKey(apiKey)) {
            repository.saveApiKey(apiKey)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Invalid API key format"))
        }
    }

    fun getApiKey(): String? {
        return repository.getApiKey()
    }

    fun clearApiKey() {
        repository.clearApiKey()
    }

    private fun isValidApiKey(apiKey: String): Boolean {
        return apiKey.length > 20 // Example validation: API keys must be longer than 20 characters
    }
}