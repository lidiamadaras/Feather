package com.example.feather.viewmodels.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.ReflectionModel
import com.example.feather.service.ReflectionService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReflectionDetailViewModel @Inject constructor(private val reflectionService: ReflectionService) : ViewModel(){

    private val _reflection = MutableLiveData<ReflectionModel?>()
    val reflection: LiveData<ReflectionModel?> = _reflection


    fun getReflectionById(id: String) {
        viewModelScope.launch {
            _reflection.value = reflectionService.getReflectionById(id)
        }
    }

}