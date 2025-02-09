package com.example.feather.repository

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
                    "keywords" to dream.keywords
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
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords") // Store keywords globally for the user
                    .document(keyword.name) // Use keyword name as the document ID to prevent duplicates

                val snapshot = keywordRef.get().await()
                if (snapshot.exists()) {
                    return Result.failure(Exception("Keyword already exists!"))
                }

                // Perform the write operation
                keywordRef.set(keyword).await()

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserKeywords(): List<KeywordModel> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords") // Global keyword list
                    .orderBy("dateAdded", Query.Direction.DESCENDING)


                val snapshot = keywordsRef.get().await()

                val keywords = snapshot.documents.mapNotNull { it.toObject(KeywordModel::class.java) }
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

    suspend fun getUserDreams(): List<DreamModel>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val dreamsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams") // Global keyword list
                    .orderBy("dateAdded", Query.Direction.DESCENDING)

                val snapshot = dreamsRef.get().await()

                snapshot.documents.mapNotNull { document ->
                    document.toObject(DreamModel::class.java)?.copy(id = document.id) // Store Firestore ID
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("DreamRepo", it) }
            emptyList()
        }
    }

    suspend fun deleteDream(dreamId: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams")
                    .document(dreamId)
                    .delete()
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error deleting dream: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDreamById(dreamId: String): DreamModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val dreamDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams")
                    .document(dreamId)
                    .get()
                    .await()

                if (dreamDoc.exists()) {
                    dreamDoc.toObject(DreamModel::class.java)?.copy(id = dreamDoc.id)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error fetching dream: ${e.message}")
            null
        }

    }

    //keywords:

    suspend fun getKeywordById(keywordId: String): KeywordModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords")
                    .document(keywordId)
                    .get()
                    .await()

                if (keywordDoc.exists()) {
                    keywordDoc.toObject(KeywordModel::class.java)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error fetching keyword: ${e.message}")
            null
        }

    }

    suspend fun deleteKeyword(keywordId: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords")
                    .document(keywordId)
                    .delete()
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error deleting keyword: ${e.message}")
            Result.failure(e)
        }
    }

}
