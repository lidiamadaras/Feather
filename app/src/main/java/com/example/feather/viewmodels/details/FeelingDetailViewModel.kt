package com.example.feather.viewmodels.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.FeelingModel
import com.example.feather.service.FeelingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeelingDetailViewModel @Inject constructor(private val feelingService: FeelingService) : ViewModel(){

    private val _feeling = MutableLiveData<FeelingModel?>()
    val feeling: LiveData<FeelingModel?> = _feeling


    fun getFeelingById(id: String) {
        viewModelScope.launch {
            _feeling.value = feelingService.getFeelingById(id)
        }
    }

}