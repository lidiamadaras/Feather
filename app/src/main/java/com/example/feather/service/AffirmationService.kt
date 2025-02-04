package com.example.feather.service

import android.util.Log
import com.example.feather.models.AffirmationModel
import com.example.feather.repository.AffirmationRepository
import com.example.feather.repository.FeelingRepository
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

}