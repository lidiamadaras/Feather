package com.example.feather.viewmodels.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.service.AffirmationService
import com.example.feather.service.auth.AuthService
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authService: AuthService) : ViewModel(){

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    private val _resetPasswordResult = MutableLiveData<Result<Unit>>()
    val resetPasswordResult: LiveData<Result<Unit>> = _resetPasswordResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = authService.login(email, password)
            _loginResult.postValue(user)
        }
    }

    fun register(firstName: String, lastName: String, dateOfBirth: String, email: String, password: String) {
        viewModelScope.launch {
            val success = authService.register(firstName, lastName, dateOfBirth, email, password)
            _registerResult.postValue(success)
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            val success = authService.sendPasswordResetEmail(email)
            _resetPasswordResult.postValue(success)
        }
    }


}