package com.example.feather.service

import com.example.feather.models.DreamModel
import com.example.feather.repository.DreamRepository

//for business logic, validation: service layer

class DreamService(private val dreamRepository: DreamRepository) {

    suspend fun saveDream(dream: DreamModel): Result<Unit> {
        // Validate title and description, they can't be empty when saving:
        if (dream.title?.isBlank() == true) return Result.failure(Exception("Title cannot be empty"))
        if (dream.description?.isBlank() == true) return Result.failure(Exception("Description cannot be empty"))

        return dreamRepository.saveDream(dream)
    }

/
}