package com.example.feather.service

import android.util.Log
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.example.feather.repository.DreamRepository
import javax.inject.Inject

//for business logic, validation: service layer

class DreamService @Inject constructor(private val dreamRepository: DreamRepository) {

    suspend fun saveDream(dream: DreamModel): Result<Unit> {
        // Validate title and description, they can't be empty when saving:
        if (dream.title?.isBlank() == true) return Result.failure(Exception("Title cannot be empty"))
        if (dream.description?.isBlank() == true) return Result.failure(Exception("Description cannot be empty"))

        return dreamRepository.saveDream(dream)
    }

    suspend fun saveKeyword(keyword: KeywordModel): Result<Unit> {
        if(keyword.name.isBlank()) return Result.failure(Exception("Keyword cannot be empty"))
        return dreamRepository.saveKeyword(keyword)
    }

    suspend fun getUserKeywords(): List<KeywordModel> {
        Log.d("keyword", dreamRepository.getUserKeywords().toString())
        return dreamRepository.getUserKeywords()
    }

    suspend fun getUserDreams(): List<DreamModel> {
        return dreamRepository.getUserDreams()
    }

    suspend fun getDreamById(dreamId: String): DreamModel? {
        if (dreamId.isBlank()) return null
        return dreamRepository.getDreamById(dreamId)
    }

    suspend fun deleteDream(dreamId: String): Result<Unit>  {
        if (dreamId.isBlank()) return Result.failure(Exception("DreamId is empty"))
        return dreamRepository.deleteDream(dreamId)
    }

    //keywords

    suspend fun getKeywordById(keywordId: String): KeywordModel? {
        if (keywordId.isBlank()) return null
        return dreamRepository.getKeywordById(keywordId)
    }

    suspend fun deleteKeyword(keywordId: String): Result<Unit>  {
        if (keywordId.isBlank()) return Result.failure(Exception("KeywordId is empty"))
        return dreamRepository.deleteKeyword(keywordId)
    }

}