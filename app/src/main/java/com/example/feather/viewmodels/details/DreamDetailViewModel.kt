package com.example.feather.viewmodels.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.example.feather.service.DreamService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DreamDetailViewModel @Inject constructor(private val dreamService: DreamService) : ViewModel(){

    private val _dream = MutableLiveData<DreamModel?>()
    val dream: LiveData<DreamModel?> = _dream


    fun getDreamById(id: String) {
        viewModelScope.launch {
            _dream.value = dreamService.getDreamById(id)
        }
    }

}