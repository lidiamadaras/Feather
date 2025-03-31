package com.example.feather.repository.ai

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.feather.models.DreamModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import android.util.Log
import com.example.feather.models.AIPersonaModel
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.ImagePart
import java.io.InputStream
import java.net.URL

import android.util.Base64
import com.example.feather.models.Content
import com.example.feather.models.GeminiRequest
import com.example.feather.models.GeminiResponse
import com.example.feather.models.GenerationConfig
import com.example.feather.models.Part
import com.example.feather.service.ai.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class AIRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

//    fun generateImage(apiKey: String) {
//
//        val requestBody = GeminiRequest(
//            contents = listOf(
//                Content(parts = listOf(Part(text = "Hi, can you create a 3D rendered image of a cockatoo with wings and a top hat flying over a happy futuristic sci-fi city with lots of greenery?")))
//            ),
//            generationConfig = GenerationConfig(responseModalities = listOf("IMAGE"))
//        )
//
//        RetrofitClient.instance.generateImage(apiKey, requestBody).enqueue(object : Callback<GeminiResponse> {
//            override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
//                if (response.isSuccessful) {
//                    val imageData = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData?.data
//                    if (imageData != null) {
//                        saveImage(imageData)
//                    } else {
//                        Log.e("GeminiAPI", "No image data found")
//                    }
//                } else {
//                    Log.e("GeminiAPI", "API request failed: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
//                Log.e("GeminiAPI", "Request failed", t)
//            }
//        })
//    }
//
//    fun saveImage(base64String: String) {
//        try {
//            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
//            val file = File("/sdcard/Download/generated_image_YAY.png") // Adjust path as needed
//            val fos = FileOutputStream(file)
//            fos.write(decodedBytes)
//            fos.close()
//            Log.d("GeminiAPI", "Image saved successfully at: ${file.absolutePath}")
//        } catch (e: Exception) {
//            Log.e("GeminiAPI", "Error saving image", e)
//        }
//    }

    suspend fun savePreferredPersona(persona: String) {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

        db.collection("users")
            .document(userId)
            .update("preferredPersona", persona)
            .await()
    }

    suspend fun loadPreferredPersona(): String? {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

        val document = db.collection("users")
            .document(userId)
            .get()
            .await()

        return document.getString("preferredPersona")
    }

    suspend fun getPersonaByName(personaName: String): AIPersonaModel?{
        return try {
                val personaDoc = db.collection("personasGemini")
                    .document(personaName)
                    .get()
                    .await()

                if (personaDoc.exists()) {
                    personaDoc.toObject(AIPersonaModel::class.java)?.copy(name = personaDoc.id)
                } else {
                    null
                }

        } catch (e: Exception) {
            Log.e("AIRepo", "Error fetching persona: ${e.message}")
            null
        }
    }

    suspend fun analyzeDream(apiKey: String, dream: DreamModel): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.5-pro-exp-03-25",
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


    suspend fun weeklyAnalysis(apiKey: String): Result<String> {
        return analyzeDreamsForPeriod(apiKey, daysBack = 7, periodName = "Weekly")
    }

    suspend fun monthlyAnalysis(apiKey: String): Result<String> {
        return analyzeDreamsForPeriod(apiKey, daysBack = 30, periodName = "Monthly")
    }

    private suspend fun analyzeDreamsForPeriod(apiKey: String, daysBack: Int, periodName: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid
                    ?: return@withContext Result.failure(Exception("User not authenticated"))

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

                val cutoffDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, -daysBack)
                }.time

                val dreams = db.collection("users")
                    .document(userId)
                    .collection("dreams")
                    .whereGreaterThan("dateAdded", cutoffDate)
                    .get()
                    .await()
                    .toObjects(DreamModel::class.java)

                if (dreams.isEmpty()) {
                    return@withContext Result.success("No dreams recorded in the past $daysBack days.")
                }

                val dreamsText = dreams.joinToString("\n\n") { dream ->
                    """
                    Title: ${dream.title}
                    Description: ${dream.description}
                    Keywords: ${dream.keywords.joinToString(", ")}
                    Category: ${dream.category}
                    """.trimIndent()
                }

                val prompt = """
                    $periodName Dream Analysis:
                    Below are the dreams recorded in the past $daysBack days.
                    
                    $dreamsText
                    
                    Provide insights into recurring themes, symbols, emotions, and potential meanings.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                Result.success(response.text ?: "No response received")

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun saveInterpretation(analysisText: String, type: String): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        if (type !in listOf("single_dream_interpretations", "weekly_interpretations", "monthly_interpretations")) {
            return Result.failure(Exception("Invalid analysis type"))
        }

        val interpretationData = mapOf(
            "analysisText" to analysisText,
            "timeAdded" to com.google.firebase.Timestamp.now()
        )

        return withContext(Dispatchers.IO) {
            try {
                db.collection("users")
                    .document(userId)
                    .collection(type)
                    .add(interpretationData)
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}