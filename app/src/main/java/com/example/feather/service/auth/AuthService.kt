package com.example.feather.service.auth

import android.util.Log
import com.example.feather.models.UserData
import com.example.feather.repository.AffirmationRepository
import com.example.feather.repository.auth.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthService @Inject constructor(private val authRepository: AuthRepository) {
    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return authRepository.login(email, password)
    }

    fun signOut() {
        authRepository.signOut()
    }

    suspend fun deleteAccount(): Result<Unit>{
        return authRepository.deleteAccount()
    }

    fun getUserData(onResult: (UserData?) -> Unit) {
        authRepository.getUserData { userData ->
            onResult(userData)
        }
    }

//    fun updateUserData(userData: UserData, onResult: (Boolean) -> Unit) {
//        authRepository.updateUserData(userData) { success ->
//            onResult(success)
//        }
//    }

    fun updateUserData(userData: UserData, onResult: (Boolean) -> Unit) {
        Log.d("AuthViewModel", "Entered updateUserData function in ViewModel")

        // Log the user data to ensure it's the correct data being passed
        Log.d("AuthViewModel", "UserData: ${userData.toString()}")

        // Call the repository to update the user data
        authRepository.updateUserData(userData) { success ->
            // Log success or failure
            if (success) {
                Log.d("AuthViewModel", "User data update succeeded.")
            } else {
                Log.d("AuthViewModel", "User data update failed.")
            }

            // Pass the result back through the callback
            onResult(success)
        }
    }


//    suspend fun loginWithGoogle(googleSignInAccount: GoogleSignInAccount): Result<Unit> {
//        return authRepository.signInWithGoogle(googleSignInAccount)
//    }

    suspend fun register(firstName: String, lastName: String, dateOfBirth: String, email: String, password: String): Result<Unit> {
        return authRepository.register(firstName, lastName, dateOfBirth, email, password)
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return authRepository.sendPasswordResetEmail(email)
    }
}