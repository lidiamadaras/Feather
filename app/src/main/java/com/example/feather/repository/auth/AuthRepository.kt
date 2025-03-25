package com.example.feather.repository.auth

import android.util.Log
import com.example.feather.models.UserData
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
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

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val user = auth.currentUser ?: return Result.failure(Exception("No user signed in"))

            db.collection("users").document(user.uid).delete().await()

            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUserData(onResult: (UserData?) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject(UserData::class.java)
                    onResult(user)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun updateUserData(userData: UserData, onResult: (Boolean) -> Unit) {
        Log.d("UserData", "entered update function in AuthRepo")
        val uid = auth.currentUser?.uid ?: return

        Log.d("UserData", userData.toString())

        db.collection("users").document(uid)
            .update(
                "firstName", userData.firstName,
                "lastName", userData.lastName,
                "dateOfBirth", userData.dateOfBirth
            )
            .addOnSuccessListener {
                Log.d("UserData", "success")
                onResult(true)
            }
            .addOnFailureListener {
                Log.d("UserData", "failure")
                onResult(false)
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