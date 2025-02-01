package com.example.feather.repository

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.DreamModel
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

                dreamRef.set(dream).await()
                Result.success(Unit)
            }
            else {return Result.failure(Exception("User not authenticated"))}
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}