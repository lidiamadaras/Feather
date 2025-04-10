package com.example.feather.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.SymbolModel
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import com.opencsv.CSVReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject

class ExploreRepository  @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
    suspend fun askAboutSymbolGemini(apiKey: String, symbol: String, promptPersona: String): Result<String>{
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
                    Analyze this symbol:
                    
                    $symbol
                    
                    Provide insight on what it could mean if this symbol occurs in a dream. Try to talk about the different interpretations for the symbol.
                    Keep your answer short, and to the point. 
                    
                    $promptPersona
                    
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)

                val text = response.text ?: "No response received"
                Result.success(text)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    suspend fun getSymbolById(symbolId: String): SymbolModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val affirmationDoc = db.collection("symbols")
                    .document(symbolId)
                    .get()
                    .await()

                if (affirmationDoc.exists()) {
                    affirmationDoc.toObject(SymbolModel::class.java)?.copy(id = affirmationDoc.id)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ExploreRepo", "Error fetching symbol: ${e.message}")
            null
        }

    }

    //handling the csv file with the symbols:

//    private fun parseCsvToSymbols(inputStream: InputStream): List<SymbolModel> {
//        val reader = CSVReader(InputStreamReader(inputStream))
//        val symbols = mutableListOf<SymbolModel>()
//
//        reader.readNext()
//
//        var line: Array<String>?
//        while (reader.readNext().also { line = it } != null) {
//            val name = line?.get(0) ?: ""
//            val description = line?.get(1) ?: ""
//
//            val symbol = SymbolModel(name = name, description = description)
//            symbols.add(symbol)
//        }
//        return symbols
//    }
//
//    private fun uploadSymbolsFromCSV(symbols: List<SymbolModel>) {
//        val symbolsCollection = db.collection("symbols")
//
//        symbols.forEach { symbol ->
//            val symbolMap = hashMapOf(
//                "name" to symbol.name,
//                "description" to symbol.description,
//                "searchCount" to symbol.searchCount,
//                "tag" to symbol.tag
//            )
//
//            symbolsCollection.add(symbolMap)
//                .addOnSuccessListener { documentReference ->
//                    Log.d("Firestore", "Document added with ID: ${documentReference.id}")
//                }
//                .addOnFailureListener { e ->
//                    Log.w("Firestore", "Error adding document", e)
//                }
//        }
//    }
//
//    fun loadCsvAndUploadToDatabase(context: Context) {
//        Log.d("Firestore", "entered repo context function")
//        val inputStream = context.assets.open("symbols.csv")
//
//        Log.d("Firestore", inputStream.toString())
//        val symbols = parseCsvToSymbols(inputStream)
//        uploadSymbolsFromCSV(symbols)
//    }


}
