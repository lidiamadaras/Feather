package com.example.feather.viewmodels.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feather.models.UserData
import com.example.feather.service.AffirmationService
import com.example.feather.service.auth.AuthService
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authService: AuthService) : ViewModel(){

    private val _loginResult = MutableLiveData<Result<Unit>>()
    val loginResult: LiveData<Result<Unit>> = _loginResult

    private val _loginWithGoogleResult = MutableLiveData<Result<Unit>>()
    val loginWithGoogleResult: LiveData<Result<Unit>> = _loginWithGoogleResult

    private val _registerResult = MutableLiveData<Result<Unit>>()
    val registerResult: LiveData<Result<Unit>> = _registerResult

    private val _resetPasswordResult = MutableLiveData<Result<Unit>>()
    val resetPasswordResult: LiveData<Result<Unit>> = _resetPasswordResult

    private val _deleteAccountStatus = MutableLiveData<Result<Unit>>()
    val deleteAccountStatus: LiveData<Result<Unit>> get() = _deleteAccountStatus

    //user data: get and update

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> get() = _userData

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> get() = _updateStatus

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = authService.login(email, password)
            _loginResult.postValue(user)
        }
    }

    fun signOut() {
        _loading.value = true
        authService.signOut()
        _loading.value = false
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _loading.value = true
            val result = authService.deleteAccount()
            _deleteAccountStatus.value = result
            _loading.value = false
        }
    }

    fun getUserData() {
        authService.getUserData { data ->
            _userData.postValue(data)
        }
    }

    fun updateUserData(userData: UserData) {
        authService.updateUserData(userData) { success ->
            Log.d("VM", "success")
            _updateStatus.postValue(success)
        }
    }

//    fun loginWithGoogle(googleSignInAccount: GoogleSignInAccount) {
//        viewModelScope.launch {
//            val userGoogle = authService.loginWithGoogle(googleSignInAccount)
//            _loginWithGoogleResult.postValue(userGoogle)
//        }
//    }

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