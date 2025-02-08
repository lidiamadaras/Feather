package com.example.feather.repository

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.AffirmationModel
import com.example.feather.models.DreamModel
import com.example.feather.models.EmotionModel
import com.example.feather.models.FeelingModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//for handling all firestore operations: repo layer

class AffirmationRepository  @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveAffirmation(affirmation: AffirmationModel): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val affirmationRef =
                    db.collection("users")
                        .document(currentUser.uid)
                        .collection("affirmations")
                        .document()

                val affirmationFields = mapOf(
                    "dateAdded" to Timestamp.now(),
                    "text" to affirmation.text
                )

                affirmationRef.set(affirmationFields).await()

                Result.success(Unit)
            }
            else {return Result.failure(Exception("User not authenticated"))}
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserAffirmations(): List<AffirmationModel>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val affirmationsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("affirmations")

                val snapshot = affirmationsRef.get().await()

                val affirmations = snapshot.documents.mapNotNull { it.toObject(AffirmationModel::class.java) }

                return affirmations
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("AffirmationRepo", it) }
            emptyList()
        }
    }

    suspend fun getRandomUserAffirmation(): AffirmationModel? {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val affirmationsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("affirmations")

                val snapshot = affirmationsRef.get().await()
                val affirmations = snapshot.documents.mapNotNull { it.toObject(AffirmationModel::class.java) }

                if (affirmations.isNotEmpty()) affirmations.random() else null
            } else {
                null
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("AffirmationRepo", it) }
            null
        }
    }

    suspend fun deleteAffirmation(affirmationId: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("affirmations")
                    .document(affirmationId)
                    .delete()
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Log.e("AffirmationRepo", "Error deleting affirmation: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAffirmationById(affirmationId: String): AffirmationModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val affirmationDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("affirmations")
                    .document(affirmationId)
                    .get()
                    .await()

                if (affirmationDoc.exists()) {
                    affirmationDoc.toObject(AffirmationModel::class.java)?.copy(id = affirmationDoc.id)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AffirmationRepo", "Error fetching affirmation: ${e.message}")
            null
        }

    }

}