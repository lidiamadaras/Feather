package com.example.feather.repository.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor()  {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun login(email: String, password: String): Result<Unit>{
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(firstName: String, lastName: String, dateOfBirth: String, email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("User ID is null"))

            val user = hashMapOf(
                "uid" to userId,
                "firstName" to firstName,
                "lastName" to lastName,
                "dateOfBirth" to dateOfBirth,
                "email" to email,
                "dateAdded" to System.currentTimeMillis()
            )

            db.collection("users").document(userId).set(user).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("Password is too weak."))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception("Invalid email format!"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("Email is already in use."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(googleSignInAccount: GoogleSignInAccount): Result<Unit> {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            val result = auth.signInWithCredential(credential).await()

            // Check if this Google user has a matching email with an existing account
            val user = result.user
            val existingUser = auth.currentUser

            // If user exists and email matches, link the accounts
            if (existingUser != null && existingUser.email == user?.email) {
                // Accounts linked successfully, proceed to app
                Result.success(Unit)
            } else {
                // Handle case where email doesn't match
                // Maybe you prompt the user to login/register
                Result.failure(Exception("Email does not match existing account"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun linkGoogleAccount(googleSignInAccount: GoogleSignInAccount): Result<FirebaseUser?> {
        return try {
            val credential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                currentUser.linkWithCredential(credential).await()
                Result.success(currentUser)
            } else {
                Result.failure(Exception("No user is signed in"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}