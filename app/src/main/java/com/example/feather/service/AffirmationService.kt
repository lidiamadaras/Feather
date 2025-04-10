package com.example.feather.service

import android.util.Log
import com.example.feather.models.AffirmationModel
import com.example.feather.models.DreamModel
import com.example.feather.repository.AffirmationRepository
import javax.inject.Inject

//for business logic, validation: service layer

class AffirmationService @Inject constructor(private val affirmationRepository: AffirmationRepository) {

    suspend fun saveAffirmation(affirmation: AffirmationModel): Result<Unit> {
        if (affirmation.text.isEmpty()) return Result.failure(Exception("Affirmation cannot be empty"))

        return affirmationRepository.saveAffirmation(affirmation)
    }

    suspend fun getUserAffirmations(): List<AffirmationModel> {
        return affirmationRepository.getUserAffirmations()
    }

    suspend fun getRandomUserAffirmation(): AffirmationModel? {
        return affirmationRepository.getRandomUserAffirmation()
    }

    suspend fun getAffirmationById(affirmationId: String): AffirmationModel? {
        if (affirmationId.isBlank()) return null
        return affirmationRepository.getAffirmationById(affirmationId)
    }

    suspend fun deleteAffirmation(affirmationId: String): Result<Unit>  {
        if (affirmationId.isBlank()) return Result.failure(Exception("AffirmationId is empty"))
        return affirmationRepository.deleteAffirmation(affirmationId)
    }

}