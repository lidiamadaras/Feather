package com.example.feather.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.DreamModel
import com.example.feather.models.EmotionModel
import com.example.feather.models.FeelingModel
import com.example.feather.service.FeelingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeelingViewModel @Inject constructor(private val feelingService: FeelingService) : ViewModel(){

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _saveEmotionResult = MutableLiveData<Result<Unit>>()
    val saveEmotionResult: LiveData<Result<Unit>> = _saveEmotionResult

    private val _userEmotions = MutableLiveData<List<EmotionModel>>()
    val userEmotions: LiveData<List<EmotionModel>> = _userEmotions

    private val _userFeelings = MutableLiveData<List<FeelingModel>>()
    val userFeelings: LiveData<List<FeelingModel>> = _userFeelings


    fun saveFeeling(feeling: FeelingModel) {
        viewModelScope.launch {
            _saveResult.value = runCatching { feelingService.saveFeeling(feeling) }
        }
    }

    fun saveEmotion(emotion: EmotionModel){
        viewModelScope.launch {
            _saveEmotionResult.value = runCatching { feelingService.saveEmotion(emotion) }
        }
    }

    fun getUserEmotions() {
        viewModelScope.launch {
            val emotions = feelingService.getUserEmotions()
            _userEmotions.value = emotions
        }
    }

    fun getUserFeelings() {
        viewModelScope.launch {
            val feelings = feelingService.getUserFeelings()
            _userFeelings.value = feelings
        }
    }


}