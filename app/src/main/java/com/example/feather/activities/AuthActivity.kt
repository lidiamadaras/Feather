package com.example.feather.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.feather.R
import com.example.feather.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth

class AuthActivity : AppCompatActivity()  {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and set the content view
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val loginButton = binding.loginButton
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            signInUser(email, password)
        }

        //password forgotten, send reset email:
        val passwordResetTextView = binding.passwordResetTextView
        val spannableString = SpannableString("Forgot password?")
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        passwordResetTextView.text = spannableString

        passwordResetTextView.setOnClickListener {
            showPasswordResetDialog()
        }


        val spannable = SpannableString("Don't have an account? Register")
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0)
        binding.registerTextView.text = spannable
        binding.registerTextView.setOnClickListener {
            // Navigate to RegisterActivity when user clicks "Register"
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "signInWithEmail:success")
                    //val user = auth.currentUser
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showPasswordResetDialog() {
        // Create the EditText for email input
        val emailEditText = EditText(this).apply {
            hint = "Enter your email"
            inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setPadding(32, 16, 32, 16)
        }

        // Create the AlertDialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Reset Password")
            //.setMessage("Enter your email to reset your password")
            .setView(emailEditText)  // Set the EditText in the dialog
            .setPositiveButton("Send Email") { _, _ ->
                val email = emailEditText.text.toString().trim()
                if (email.isNotEmpty()) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)  // Cancel button to dismiss the dialog
            .create()

        dialog.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }


}