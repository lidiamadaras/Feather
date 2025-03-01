package com.example.feather.repository.ai

import com.example.feather.ai.SecureStorage
import javax.inject.Inject

class ApiKeyRepository @Inject constructor(private val secureStorage: SecureStorage) {

    fun saveApiKey(apiKey: String) {
        secureStorage.saveApiKey(apiKey)
    }

    fun getApiKey(): String? {
        return secureStorage.getApiKey()
    }

    fun clearApiKey() {
        secureStorage.clearApiKey()
    }
}
