package com.example.feather.service

import android.util.Log
import com.example.feather.models.EmotionModel
import com.example.feather.models.FeelingModel
import com.example.feather.repository.FeelingRepository
import javax.inject.Inject

//for business logic, validation: service layer

class FeelingService @Inject constructor(private val feelingRepository: FeelingRepository) {

    suspend fun saveFeeling(feeling: FeelingModel): Result<Unit> {
        // Validate emotion - one has to be selected to save
        if (feeling.emotion.isEmpty()) return Result.failure(Exception("Emotion has to be selected"))

        return feelingRepository.saveFeeling(feeling)
    }

    suspend fun saveEmotion(emotion: EmotionModel): Result<Unit> {
        if(emotion.name.isBlank()) return Result.failure(Exception("Emotion cannot be empty"))
        return feelingRepository.saveEmotion(emotion)
    }

    suspend fun getUserEmotions(): List<EmotionModel> {
        return feelingRepository.getUserEmotions()
    }

}