package com.example.feather.repository

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//for handling all firestore operations: repo layer

class DreamRepository  @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveDream(dream: DreamModel): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val dreamRef =
                    db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams")
                    .document()

                val dreamFields = mapOf(
                    "dateAdded" to Timestamp.now(), // Automatically get the current timestamp
                    "description" to dream.description,
                    "category" to dream.category,
                    "hoursSlept" to dream.hoursSlept,
                    "isRecurring" to dream.isRecurring,
                    "title" to dream.title,
                )

                dreamRef.set(dreamFields).await()

                Result.success(Unit)
            }
            else {return Result.failure(Exception("User not authenticated"))}
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveKeyword(keyword: KeywordModel): Result<Unit>{
        Log.d("keyword", keyword.toString())
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords") // Store keywords globally for the user
                    .document(keyword.name) // Use keyword name as the document ID to prevent duplicates

                Log.d("keyword", "Attempting to save keyword: ${keyword.name}")

                // Perform the write operation
                keywordRef.set(keyword).await()

                // Log after successful save
                Log.d("keyword added", "Keyword saved successfully: ${keyword.name}")
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserKeywords(): List<KeywordModel> {
        Log.d("keyword", "entered getuserkeywords repo")
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords") // Global keyword list

                Log.d("keyword", currentUser.uid)

                val snapshot = keywordsRef.get().await()
                Log.d("keyword", "Snapshot size: ${snapshot.size()}")           //currently 0

                //Log.d("keyword", snapshot.toString())             //ez mar nem irodik ki
                val keywords = snapshot.documents.mapNotNull { it.toObject(KeywordModel::class.java) }
                Log.d("keyword", keywords.toString())
                return keywords
            } else {
                Log.d("keyword", "there are no keywords when fetching from repo")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("KeywordRepository", "Error fetching keywords: ${e.message}", e)
            emptyList()
        }
    }

}