package com.example.feather.service

import android.util.Log
import com.example.feather.models.DreamModel
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

    suspend fun doesEmotionExist(name: String): Boolean{
        return feelingRepository.doesEmotionExist(name)
    }

    suspend fun getUserEmotions(): List<EmotionModel> {
        return feelingRepository.getUserEmotions()
    }

    suspend fun getUserFeelings(): List<FeelingModel> {
        return feelingRepository.getUserFeelings()
    }

    suspend fun getFeelingById(feelingId: String): FeelingModel? {
        if (feelingId.isBlank()) return null
        return feelingRepository.getFeelingById(feelingId)
    }

    suspend fun deleteFeeling(feelingId: String): Result<Unit>  {
        if (feelingId.isBlank()) return Result.failure(Exception("FeelingId is empty"))
        Log.d("feelid", "not empty")
        return feelingRepository.deleteFeeling(feelingId)
    }

    //emotions

    suspend fun getEmotionById(emotionId: String): EmotionModel? {
        if (emotionId.isBlank()) return null
        return feelingRepository.getEmotionById(emotionId)
    }

    suspend fun deleteEmotion(emotionId: String): Result<Unit>  {
        if (emotionId.isBlank()) return Result.failure(Exception("EmotionId is empty"))
        return feelingRepository.deleteEmotion(emotionId)
    }

}