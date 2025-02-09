package com.example.feather.viewmodels

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
class AffirmationViewModel @Inject constructor(private val affirmationService: AffirmationService) : ViewModel(){

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> = _saveResult

    private val _userAffirmations = MutableLiveData<List<AffirmationModel>>()
    val userAffirmations: LiveData<List<AffirmationModel>> = _userAffirmations

    private val _randomUserAffirmation = MutableLiveData<AffirmationModel?>()
    val randomUserAffirmation: LiveData<AffirmationModel?> = _randomUserAffirmation

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult


    fun saveAffirmation(affirmation: AffirmationModel) {
        viewModelScope.launch {
            _saveResult.value = runCatching { affirmationService.saveAffirmation(affirmation) }
        }
    }

    fun getUserAffirmations() {
        viewModelScope.launch {
            val affirmations = affirmationService.getUserAffirmations()
            _userAffirmations.value = affirmations
        }
    }

    fun getRandomUserAffirmation() {
        viewModelScope.launch {
            val affirmation = affirmationService.getRandomUserAffirmation()
            _randomUserAffirmation.value = affirmation
        }
    }

    fun deleteAffirmation(affirmationId: String) {
        viewModelScope.launch {
            _deleteResult.value = runCatching { affirmationService.deleteAffirmation(affirmationId) }
        }
    }



}