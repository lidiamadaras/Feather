package com.example.feather.viewmodels.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.AffirmationModel
import com.example.feather.service.AffirmationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AffirmationDetailViewModel @Inject constructor(private val affirmationService: AffirmationService) : ViewModel(){

    private val _affirmation = MutableLiveData<AffirmationModel?>()
    val affirmation: LiveData<AffirmationModel?> = _affirmation


    fun getAffirmationById(id: String) {
        viewModelScope.launch {
            _affirmation.value = affirmationService.getAffirmationById(id)
        }
    }

}