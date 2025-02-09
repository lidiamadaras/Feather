package com.example.feather.viewmodels.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.EmotionModel
import com.example.feather.service.FeelingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmotionDetailViewModel @Inject constructor(private val feelingService: FeelingService) : ViewModel(){

    private val _emotion = MutableLiveData<EmotionModel?>()
    val emotion: LiveData<EmotionModel?> = _emotion


    fun getEmotionById(id: String) {
        viewModelScope.launch {
            _emotion.value = feelingService.getEmotionById(id)
        }
    }

}