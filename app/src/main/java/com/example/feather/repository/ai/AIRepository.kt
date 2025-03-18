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


class AIRepository @Inject constructor(
    private val httpClient: OkHttpClient
) {


    suspend fun analyzeDream(apiKey: String, dream: DreamModel): Result<String> {
//        val generativeModel = GenerativeModel(
//            modelName = "gemini-1.5-flash",
//            apiKey = apiKey
//        )
        return withContext(Dispatchers.IO) {  // Ensure it runs on a background thread
            val prompt = """
            Analyze this dream:
            Title: ${dream.title}
            Description: ${dream.description}
            Keywords: ${dream.keywords.joinToString(", ")}
            Category: ${dream.category}
        """.trimIndent()

            try {
                val requestBody = """
                {
                    "model": "gemini-pro",
                    "prompt": "$prompt",
                    "max_tokens": 500
                }
            """.trimIndent()

                val request = Request.Builder()
                    //.url("https://api.gemini.google.com/v1/your-endpoint?key=$apiKey")
                    //.url("https://generativelanguage.googleapis.com/v1/models/gemini-pro:generateContent?key=$apiKey")
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
                    Result.success(responseBody)
                } else {
                    Result.failure(Exception("API call failed: ${response.message}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}