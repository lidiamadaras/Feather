package com.example.feather.service

import android.content.Context
import android.util.Log
import com.example.feather.models.SymbolModel
import com.example.feather.repository.ExploreRepository
import javax.inject.Inject
import kotlin.math.exp

class ExploreService @Inject constructor(private val exploreRepository: ExploreRepository) {

    suspend fun getSymbols(): List<SymbolModel> {
        return exploreRepository.getSymbols()
    }

    suspend fun getSymbolById(symbolId: String): SymbolModel?{
        if (symbolId.isBlank()) return null
        return exploreRepository.getSymbolById(symbolId)
    }

//    fun loadCSVSymbols(context: Context){
//        Log.d("Firestore", "entered service function")
//        exploreRepository.loadCsvAndUploadToDatabase(context)
//    }


//    suspend fun getAffirmationById(affirmationId: String): AffirmationModel? {
//        if (affirmationId.isBlank()) return null
//        return affirmationRepository.getAffirmationById(affirmationId)
//    }

}