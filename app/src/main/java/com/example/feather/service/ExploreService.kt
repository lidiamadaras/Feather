package com.example.feather.service

import android.util.Log
import com.example.feather.models.SymbolModel
import com.example.feather.repository.ExploreRepository
import javax.inject.Inject

class ExploreService @Inject constructor(private val exploreRepository: ExploreRepository) {

    suspend fun getSymbols(): List<SymbolModel> {
        return exploreRepository.getSymbols()
    }


//    suspend fun getAffirmationById(affirmationId: String): AffirmationModel? {
//        if (affirmationId.isBlank()) return null
//        return affirmationRepository.getAffirmationById(affirmationId)
//    }

}