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
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.databinding.ActivityAuthBinding
import com.example.feather.viewmodels.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityAuthBinding
    private val authViewModel : AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and set the content view
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton = binding.loginButton
        val emailEditText = binding.emailEditText
        val passwordEditText = binding.passwordEditText

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            authViewModel.login(email, password)
        }

        authViewModel.loginResult.observe(this) { result ->
            result.onSuccess  {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            result.onFailure{ exception ->
                Toast.makeText(this, "Login failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        //password forgotten, send reset email:
        val passwordResetTextView = binding.passwordResetTextView
        val spannableString = SpannableString("Forgot password?")
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        passwordResetTextView.text = spannableString

        passwordResetTextView.setOnClickListener {
            showPasswordResetDialog()
        }

        authViewModel.resetPasswordResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Password reset email sent!", Toast.LENGTH_SHORT).show()
            }
            result.onFailure { exception ->
                Toast.makeText(this, "Failed to send reset email: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }


        val spannable = SpannableString("Don't have an account? Register")
        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0)
        binding.registerTextView.text = spannable
        binding.registerTextView.setOnClickListener {
            // Navigate to RegisterActivity when user clicks "Register"
            startActivity(Intent(this, RegisterActivity::class.java))
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
                    authViewModel.sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)  // Cancel button to dismiss the dialog
            .create()

        dialog.show()
    }
}