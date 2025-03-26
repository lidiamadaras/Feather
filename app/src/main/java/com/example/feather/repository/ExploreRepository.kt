package com.example.feather.repository

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.SymbolModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ExploreRepository  @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getSymbols(): List<SymbolModel> {
        return try {
            val symbolsRef = db.collection("symbols")
                .orderBy("name", Query.Direction.ASCENDING)

            val snapshot = symbolsRef.get().await()

            val symbols = snapshot.documents.mapNotNull { document ->
                document.toObject(SymbolModel::class.java)?.copy(id = document.id)
            }

            return symbols
        } catch (e: Exception) {
            e.message?.let { Log.e("ExploreRepo", it) }
            emptyList()
        }
}


//    suspend fun getAffirmationById(affirmationId: String): AffirmationModel?{
//        return try {
//            val currentUser = auth.currentUser
//            if (currentUser != null) {
//                val affirmationDoc = db.collection("users")
//                    .document(currentUser.uid)
//                    .collection("affirmations")
//                    .document(affirmationId)
//                    .get()
//                    .await()
//
//                if (affirmationDoc.exists()) {
//                    affirmationDoc.toObject(AffirmationModel::class.java)?.copy(id = affirmationDoc.id)
//                } else {
//                    null
//                }
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("AffirmationRepo", "Error fetching affirmation: ${e.message}")
//            null
//        }
//
//    }
}
