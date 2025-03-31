package com.example.feather.service.ai

//for generate image:
import com.example.feather.models.GeminiRequest
import com.example.feather.models.GeminiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
interface GeminiApiService {
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.0-flash-exp-image-generation:generateContent")
    fun generateImage(
        @Query("key") apiKey: String,
        @Body requestBody: GeminiRequest
    ): Call<GeminiResponse>
}