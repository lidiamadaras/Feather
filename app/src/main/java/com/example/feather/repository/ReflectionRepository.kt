package com.example.feather.repository

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.DreamModel
import com.example.feather.models.ReflectionModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//for handling all firestore operations: repo layer

class ReflectionRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveReflection(reflection: ReflectionModel): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val reflectionRef =
                    db.collection("users")
                        .document(currentUser.uid)
                        .collection("reflections")
                        .document()

                val reflectionFields = mapOf(
                    "dateAdded" to Timestamp.now(),
                    "text" to reflection.text
                )

                reflectionRef.set(reflectionFields).await()

                Result.success(Unit)
            }
            else {return Result.failure(Exception("User not authenticated"))}
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserReflections(): List<ReflectionModel>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val reflectionsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("reflections")

                val snapshot = reflectionsRef.get().await()

                val reflections = snapshot.documents.mapNotNull { it.toObject(ReflectionModel::class.java) }

                return reflections
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("ReflectionRepo", it) }
            emptyList()
        }
    }

    suspend fun deleteReflection(reflectionId: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("reflections")
                    .document(reflectionId)
                    .delete()
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Log.e("ReflectionRepo", "Error deleting reflection: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getReflectionById(reflectionId: String): ReflectionModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val reflectionDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("reflections")
                    .document(reflectionId)
                    .get()
                    .await()

                if (reflectionDoc.exists()) {
                    reflectionDoc.toObject(ReflectionModel::class.java)?.copy(id = reflectionDoc.id)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ReflectionRepo", "Error fetching reflection: ${e.message}")
            null
        }

    }
}