package com.example.feather.repository.ai

import com.example.feather.models.DreamModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import okhttp3.RequestBody.Companion.toRequestBody
//import com.google.firebase.vertexai.GenerativeModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig


class AIRepository @Inject constructor(
) {
    suspend fun analyzeDream(apiKey: String, dream: DreamModel): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-1.5-flash",
                    apiKey = apiKey,
                    generationConfig = generationConfig {
                        temperature = 1f
                        topK = 64
                        topP = 0.95f
                        maxOutputTokens = 8000
                        responseMimeType = "text/plain"
                    }
                )

                val prompt = """
                    Analyze this dream:
                    Title: ${dream.title}
                    Description: ${dream.description}
                    Keywords: ${dream.keywords.joinToString(", ")}
                    Category: ${dream.category}
                    Provide insights on symbolism and meaning.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)

                val text = response.text ?: "No response received"
                Result.success(text)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}