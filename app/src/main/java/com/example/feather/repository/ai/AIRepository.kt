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
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.Part
import com.google.ai.client.generativeai.type.ImagePart
import java.io.InputStream
import java.net.URL

import com.google.ai.client.generativeai.type.Content
import kotlin.reflect.full.memberProperties


class AIRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun generateImageOfDream(apiKey: String, dream: DreamModel): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash-exp-image-generation",
                    apiKey = apiKey
                )

                //Analyze this dream and based on the description and the analysis, the themes and symbols found in the dream, create a prompt for image generation, then use this prompt to generate a realistic, deep image that describes this dream.

                val prompt = """
                    Generate a deep, surreal, and symbolic image based on the following dream description.
                Ensure it captures the themes, symbols, and emotions of the dream in a visually realistic way.
                    Title: ${dream.title}
                    Description: ${dream.description}
                    Keywords: ${dream.keywords.joinToString(", ")}
                    Category: ${dream.category}
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                Log.d("ImageGenerator candidates", response.candidates.toString())

                for (part in response.candidates[0].content.parts){
                    Log.d("ImageGenerator part", part.toString())
                }

                if (response.candidates.isNotEmpty()) {
                    for (part: Part in response.candidates[0].content.parts) {

                        Log.d("ImageGenerator", "--- Processing Part ---")
                        Log.d("ImageGenerator", "Part Actual Class: ${part::class.java.simpleName}")

                        // --- Reflection Start ---
                        Log.d("ImageGenerator", "Reflecting on Part properties:")
                        try {
                            // Get all member properties using Kotlin reflection
                            val properties = part::class.memberProperties
                            if (properties.isEmpty()) {
                                Log.d("ImageGenerator", "  >> No member properties found via Kotlin reflection.")
                                // Fallback: Try Java reflection (might show private fields)
                                Log.d("ImageGenerator", "  >> Trying Java reflection for fields:")
                                part::class.java.declaredFields.forEach { field ->
                                    Log.d("ImageGenerator", "    Java Field: ${field.name} (Type: ${field.type.simpleName})")
                                }
                            } else {
                                properties.forEach { prop ->
                                    Log.d("ImageGenerator", "  Property: ${prop.name} (Type: ${prop.returnType})")
                                    // Attempt to get the value (might fail if not accessible)
                                    try {
                                        // NOTE: Getting value via reflection can be slow and might fail
                                        // val value = prop.getter.call(part)
                                        // Log.d("ImageGenerator", "    Value: $value") // Careful logging value, could be large (like image data)
                                    } catch (e: Exception) {
                                        Log.w("ImageGenerator", "    Could not get value for ${prop.name}: ${e.message}")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("ImageGenerator", "Reflection failed: ${e.message}")
                        }
                        // --- Reflection End ---


                        // Keep the 'when' block for structure, but rely on reflection results for now
                        when (part) {
                            is TextPart -> {
                                Log.d("ImageGenerator", "Confirmed TextPart. Text: ${part.text}")
                            }
                            is ImagePart -> {
                                Log.d("ImageGenerator", "Confirmed ImagePart. Now check reflected properties for data.")
                                // PREVIOUSLY FAILED CODE: if (part.inlineData != null) { ... }
                                // NOW: Look at the reflection log output above to find the correct property name
                                // e.g., if reflection showed a property named 'imageDataBytes', you'd try accessing that.

                                // --- !!! Placeholder: Adapt based on Reflection Output !!! ---
                                // Example: If reflection showed a property 'imageData' of type ByteArray
                                /*
                                try {
                                   // Use Java reflection to access if needed, replace 'imageData' with actual name
                                   val field = part::class.java.getDeclaredField("imageData") // Replace "imageData"
                                   field.isAccessible = true // Allow access to private/internal fields
                                   val imageData = field.get(part) as? ByteArray // Cast to expected type

                                   if (imageData != null) {
                                       Log.d("ImageGenerator", "Successfully accessed data via reflection!")
                                       val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                                       if (bitmap != null) {
                                           Log.d("ImageGenerator", "Bitmap decoded successfully!")
                                           return@withContext bitmap
                                       } else {
                                           Log.e("ImageGenerator", "Failed to decode Bitmap from reflected data")
                                           return@withContext null
                                       }
                                   } else {
                                       Log.w("ImageGenerator", "Reflected data was null or wrong type.")
                                       return@withContext null
                                   }
                                } catch (e: NoSuchFieldException) {
                                     Log.e("ImageGenerator", "Reflection: Field not found - check property name!")
                                     return@withContext null
                                } catch (e: Exception) {
                                     Log.e("ImageGenerator", "Reflection access error: ${e.message}", e)
                                     return@withContext null
                                }
                                */
                                // --- !!! End Placeholder !!! ---

                            }
                            else -> {
                                Log.w("ImageGenerator", "Unknown/Unhandled Part type: ${part::class.java.simpleName}")
                            }
                        }
                        Log.d("ImageGenerator", "--- Finished Processing Part ---")
                    }
                    Log.w("ImageGenerator", "Loop finished, no suitable image part processed successfully.")
                    return@withContext null

                } else {
                    Log.w("ImageGenerator", "Response candidates is empty!")
                    return@withContext null
                }

            } catch (e: Exception) {
                Log.e("ImageGenerator error e", "Error generating image: ${e.message}")
                return@withContext null
            }
        }
    }

    private fun loadImageFromUrl(imageUrl: String): Bitmap? {
        return try {
            val inputStream: InputStream = URL(imageUrl).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            Log.e("ImageGenerator error loading from url", "Error loading image from URL: ${e.message}")
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