package com.example.feather.repository

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.EmotionModel
import com.example.feather.models.FeelingModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//for handling all firestore operations: repo layer

class FeelingRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveFeeling(feeling: FeelingModel): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val feelingRef =
                    db.collection("users")
                        .document(currentUser.uid)
                        .collection("feelings")
                        .document()

                val feelingFields = mapOf(
                    "dateAdded" to Timestamp.now(),
                    "timeStarted" to feeling.timeStarted,
                    "timeEnded" to feeling.timeEnded,
                    "intensity" to feeling.intensity,
                    "emotion" to feeling.emotion
                )

                feelingRef.set(feelingFields).await()

                Result.success(Unit)
            }
            else {return Result.failure(Exception("User not authenticated"))}
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveEmotion(emotion: EmotionModel): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val emotionRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("emotions")
                    .document(emotion.name)

                // Perform the write operation
                emotionRef.set(emotion).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("FeelingRepo", it) }
            Result.failure(e)
        }
    }

    suspend fun getUserEmotions(): List<EmotionModel> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val emotionsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("emotions") // Global keyword list

                val snapshot = emotionsRef.get().await()

                val emotions = snapshot.documents.mapNotNull { it.toObject(EmotionModel::class.java) }

                return emotions
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("FeelingRepo", it) }
            emptyList()
        }
    }

}