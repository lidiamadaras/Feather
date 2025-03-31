package com.example.feather.models

import com.google.gson.annotations.SerializedName

data class GeminiRequest(
    @SerializedName("model") val model: String,
    @SerializedName("contents") val contents: List<Content>,
    @SerializedName("generationConfig") val generationConfig: GenerationConfig
)

data class Content(
    @SerializedName("parts") val parts: List<Part>
)

data class Part(
    @SerializedName("text") val text: String
)

data class GenerationConfig(
    @SerializedName("responseModalities") val responseModalities: List<String>
)

// API Response
data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<Candidate>
)

data class Candidate(
    @SerializedName("content") val content: ContentResponse
)

data class ContentResponse(
    @SerializedName("parts") val parts: List<PartResponse>
)

data class PartResponse(
    @SerializedName("inlineData") val inlineData: InlineData
)

data class InlineData(
    @SerializedName("data") val data: String // Base64-encoded image
)
