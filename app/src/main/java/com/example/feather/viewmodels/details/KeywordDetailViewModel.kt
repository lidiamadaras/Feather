package com.example.feather.viewmodels.details

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.KeywordModel
import com.example.feather.service.DreamService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KeywordDetailViewModel @Inject constructor(private val dreamService: DreamService) : ViewModel(){

    private val _keyword = MutableLiveData<KeywordModel?>()
    val keyword: LiveData<KeywordModel?> = _keyword


    fun getKeywordById(id: String) {
        viewModelScope.launch {
            _keyword.value = dreamService.getKeywordById(id)
        }
    }

}