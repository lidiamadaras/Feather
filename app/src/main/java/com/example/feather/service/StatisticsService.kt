package com.example.feather.service

import android.util.Log
import com.example.feather.repository.StatisticsRepository
import javax.inject.Inject


class StatisticsService @Inject constructor(val repository: StatisticsRepository) {

    suspend inline fun <reified T> getLogsForDay(
        collectionName: String,
        day: Int,
        month: Int,
        year: Int
    ): List<T> {
        return repository.getLogsForDay<T>(collectionName, day, month, year)
    }

}