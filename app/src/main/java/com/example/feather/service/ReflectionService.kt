package com.example.feather.service

import android.util.Log
import com.example.feather.models.DreamModel
import com.example.feather.models.ReflectionModel
import com.example.feather.repository.ReflectionRepository
import javax.inject.Inject

//for business logic, validation: service layer

class ReflectionService @Inject constructor(private val reflectionRepository: ReflectionRepository) {

    suspend fun saveReflection(reflection: ReflectionModel): Result<Unit> {
        if (reflection.text.isEmpty()) return Result.failure(Exception("Reflection cannot be empty"))

        return reflectionRepository.saveReflection(reflection)
    }

    suspend fun getUserReflections(): List<ReflectionModel> {
        return reflectionRepository.getUserReflections()
    }

    suspend fun getReflectionById(reflectionId: String): ReflectionModel? {
        if (reflectionId.isBlank()) return null
        return reflectionRepository.getReflectionById(reflectionId)
    }

    suspend fun deleteReflection(reflectionId: String): Result<Unit>  {
        if (reflectionId.isBlank()) return Result.failure(Exception("ReflectionId is empty"))
        return reflectionRepository.deleteReflection(reflectionId)
    }
}