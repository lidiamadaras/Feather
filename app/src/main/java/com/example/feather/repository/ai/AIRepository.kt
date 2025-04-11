package com.example.feather.repository.ai

import com.example.feather.models.DreamModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*
import android.util.Log
import com.example.feather.models.AIPersonaModel

import android.util.Base64
import com.example.feather.models.Content
import com.example.feather.models.DreamInterpretationModel
import com.example.feather.models.GeminiRequest
import com.example.feather.models.GeminiResponse
import com.example.feather.models.GenerationConfig
import com.example.feather.models.Part
import com.example.feather.service.ai.RetrofitClient
import com.google.firebase.firestore.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream


class AIRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

     fun generateImage(apiKey: String, prompt: String) {
        val requestBody = GeminiRequest(
            model = "gemini-2.0-flash-exp-image-generation",
            contents = listOf(
                Content(parts = listOf(Part(text = "Hi, can you create a 3D rendered image of a cockatoo with wings and a top hat flying over a happy futuristic sci-fi city with lots of greenery?")))
            ),
            generationConfig = GenerationConfig(responseModalities = listOf("TEXT","IMAGE"))
        )
        RetrofitClient.instance.generateImage(apiKey, requestBody).enqueue(object :
            Callback<GeminiResponse> {
            override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
                if (response.isSuccessful) {
                    val imageData = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.inlineData?.data
                    if (imageData != null) {
                        saveImage(imageData)
                    } else {
                        Log.e("GeminiAPI", "No image data found")
                    }
                } else {
                    Log.e("GeminiAPI", "API request failed: ${response.errorBody()?.string()}")
                }
            }
            override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
                Log.e("GeminiAPI", "Request failed", t)
            }
        })
    }

    fun saveImage(base64String: String): String? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val file = File("/data/data/com.example.feather/files/generated_image_YAY2.png") // Adjust path as needed

            FileOutputStream(file).use { fos ->
                fos.write(decodedBytes)
            }

            Log.d("GeminiAPI", "Image saved successfully at: ${file.absolutePath}")
            file.absolutePath // Return the saved file path
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Error saving image", e)
            null // Return null in case of failure
        }
    }

    suspend fun savePreferredPersona(persona: String) {
        Log.d("Persona", "persona passed string to repo save: $persona")
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")

        Log.d("Persona", "entered savepersona in repo")

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

    suspend fun analyzeDream(apiKey: String, dream: DreamModel, personaPrompt: String): Result<String> {
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
                    At the end of the analysis, ask occasional reflective questions to help the dreamer engage in their own inner exploration.

                    
                    $personaPrompt
                    
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)

                val text = response.text ?: "No response received"
                Result.success(text)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun weeklyAnalysis(apiKey: String, prompt: String): Result<String> {
        return analyzeDreamsForPeriod(apiKey, daysBack = 7, periodName = "Weekly", prompt)
    }

    suspend fun monthlyAnalysis(apiKey: String, prompt: String): Result<String> {
        return analyzeDreamsForPeriod(apiKey, daysBack = 30, periodName = "Monthly", prompt)
    }

    private suspend fun analyzeDreamsForPeriod(apiKey: String, daysBack: Int, periodName: String, personaPrompt: String): Result<String> {
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
                    At the end of the analysis, ask occasional reflective questions to help the dreamer engage in their own inner exploration.

                    
                    $personaPrompt
                    
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                Result.success(response.text ?: "No response received")

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun saveInterpretation(analysisText: String, type: String, persona: String, title: String): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        if (type !in listOf("single_dream_interpretations", "weekly_interpretations", "monthly_interpretations")) {
            return Result.failure(Exception("Invalid analysis type"))
        }

        val interpretationData = mapOf(
            "analysisText" to analysisText,
            "timeAdded" to com.google.firebase.Timestamp.now(),
            "personaGemini" to persona,
            "title" to title
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

    suspend fun getUserInterpretations(type: String): List<DreamInterpretationModel> {
        return try {
            Log.d("Type", type)
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val interpretationsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection(type) // dynamic type: "single_dream_interpretations", etc.
                    .orderBy("timeAdded", Query.Direction.DESCENDING)

                val snapshot = interpretationsRef.get().await()

                snapshot.documents.mapNotNull { document ->
                    document.toObject(DreamInterpretationModel::class.java)?.copy(id = document.id)
                }


            } else {
                Log.d("Type", "current user null")
                emptyList()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("AIRepo", it) }
            emptyList()
        }
    }

    suspend fun deleteInterpretation(id: String, type: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users")
                    .document(currentUser.uid)
                    .collection(type)
                    .document(id)
                    .delete()
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Log.e("AIRepo", "Error deleting analysis: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getInterpretationById(id: String, type: String): DreamInterpretationModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val dreamDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection(type)
                    .document(id)
                    .get()
                    .await()

                if (dreamDoc.exists()) {
                    dreamDoc.toObject(DreamInterpretationModel::class.java)?.copy(id = dreamDoc.id)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AIRepo", "Error fetching interpretation: ${e.message}")
            null
        }

    }


}