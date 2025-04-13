package com.example.feather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.AffirmationModel
import com.example.feather.models.DreamModel
import com.example.feather.models.FeelingModel
import com.example.feather.models.ReflectionModel
import com.example.feather.service.StatisticsService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(val service: StatisticsService) : ViewModel(){

    private val _reflectionsForDay = MutableLiveData<List<ReflectionModel>>()
    val reflectionsForDay: LiveData<List<ReflectionModel>> = _reflectionsForDay

    private val _dreamsForDay = MutableLiveData<List<DreamModel>>()
    val dreamsForDay: LiveData<List<DreamModel>> = _dreamsForDay

    private val _affirmationsForDay = MutableLiveData<List<AffirmationModel>>()
    val affirmationsForDay: LiveData<List<AffirmationModel>> = _affirmationsForDay

    private val _feelingsForDay = MutableLiveData<List<FeelingModel>>()
    val feelingsForDay: LiveData<List<FeelingModel>> = _feelingsForDay


    fun fetchReflectionsForDay(day: Int, month: Int, year: Int) {
        viewModelScope.launch {
            val result = service.getLogsForDay<ReflectionModel>("reflections", day, month, year)
            _reflectionsForDay.value = result
        }
    }

    fun fetchDreamsForDay(day: Int, month: Int, year: Int) {
        viewModelScope.launch {
            val result = service.getLogsForDay<DreamModel>("dreams", day, month, year)
            _dreamsForDay.value = result
        }
    }

    fun fetchAffirmationsForDay(day: Int, month: Int, year: Int) {
        viewModelScope.launch {
            val result = service.getLogsForDay<AffirmationModel>("affirmations", day, month, year)
            _affirmationsForDay.value = result
        }
    }

    fun fetchFeelingsForDay(day: Int, month: Int, year: Int) {
        viewModelScope.launch {
            val result = service.getLogsForDay<FeelingModel>("feelings", day, month, year)
            _feelingsForDay.value = result
        }
    }


}