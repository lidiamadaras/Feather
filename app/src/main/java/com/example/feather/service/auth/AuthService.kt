package com.example.feather.service.auth

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

    suspend fun loginWithGoogle(googleSignInAccount: GoogleSignInAccount): Result<Unit> {
        return authRepository.signInWithGoogle(googleSignInAccount)
    }

    suspend fun register(firstName: String, lastName: String, dateOfBirth: String, email: String, password: String): Result<Unit> {
        return authRepository.register(firstName, lastName, dateOfBirth, email, password)
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return authRepository.sendPasswordResetEmail(email)
    }
}