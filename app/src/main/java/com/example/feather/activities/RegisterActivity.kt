package com.example.feather.activities

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.feather.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.*
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        binding.dateOfBirthEditText.setOnClickListener { showDatePicker() }

//        binding.registerButton.setOnClickListener {
//            val email = binding.emailEditText.text.toString().trim()
//            val password = binding.passwordEditText.text.toString().trim()
//            registerUser(email, password)
//        }

        binding.registerButton.setOnClickListener {
            val firstName = binding.firstNameEditText.text.toString().trim()
            val lastName = binding.lastNameEditText.text.toString().trim()
            val dateOfBirth = binding.dateOfBirthEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || dateOfBirth.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(firstName, lastName, dateOfBirth, email, password)
        }

    }

    private fun showDatePicker() {
        val calendar = getInstance()
        val year = calendar.get(YEAR)
        val month = calendar.get(MONTH)
        val day = calendar.get(DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            binding.dateOfBirthEditText.setText(formattedDate)
        }, year, month, day)

        datePickerDialog.show()
    }


//    private fun registerUser(email: String, password: String) {
//        auth.createUserWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    Log.d(TAG, "createUserWithEmail:success")
//                    //val user = auth.currentUser
//                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                } else {
//                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
//                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }

    private fun registerUser(firstName: String, lastName: String, dateOfBirth: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val timestamp = System.currentTimeMillis()
                    val dateAdded = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                        Date(timestamp)
                    )

                    if (userId != null) {
                        val user = hashMapOf(
                            "firstName" to firstName,
                            "lastName" to lastName,
                            "dateOfBirth" to dateOfBirth,
                            "email" to email,
                            "dateAdded" to dateAdded
                        )

                        db.collection("users").document(userId).set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

                                // Redirect to MainActivity
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
