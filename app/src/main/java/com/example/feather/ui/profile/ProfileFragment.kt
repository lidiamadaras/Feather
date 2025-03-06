package com.example.feather.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.feather.activities.AuthActivity
import com.example.feather.databinding.FragmentProfileBinding
import com.example.feather.viewmodels.ai.ApiKeyViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up Sign-out button listener
        binding.signOutButton.setOnClickListener {
            signOut()
        }

        binding.HomeTitleTextView.text = "Profile"

        val deleteAccountButton = binding.deleteAccountButton
        deleteAccountButton.setOnClickListener {
            //deleteUserAccount()
        }


        //api key:

        apiKeyViewModel.apiKey.observe(viewLifecycleOwner) { key ->
            binding.etApiKey.setText(key ?: "")

            if (!key.isNullOrBlank()) {
                Toast.makeText(requireContext(), "API Key saved successfully!", Toast.LENGTH_SHORT).show()
            }
        }

        apiKeyViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSaveApiKey.setOnClickListener {
            val apiKey = binding.etApiKey.text.toString().trim()
            apiKeyViewModel.saveApiKey(apiKey)
        }
        apiKeyViewModel.loadApiKey()

        val apiKey = apiKeyViewModel.apiKey.value
        Log.d("apikey", "Current API Key: $apiKey")
    }

    private fun signOut() {
        //auth.signOut()
        // Redirect to AuthActivity for login
        startActivity(Intent(requireContext(), AuthActivity::class.java))
        activity?.finish()  // Close ProfileFragment or any previous activity
    }

//    private fun deleteUserAccount() {
//        // Get the current user
//        val user = auth.currentUser
//        if (user != null) {
//            // First, delete the user's document from Firestore
//            val userRef = firestore.collection("users").document(user.uid)
//
//            userRef.delete()
//                .addOnSuccessListener {
//                    // If the user's data is deleted from Firestore, now delete the user from Firebase Auth
//                    user.delete()
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
//                                // Account deletion successful, notify user
//                                Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
//                                // Redirect to the login screen or a different activity
//                                navigateToLogin()
//                            } else {
//                                Toast.makeText(requireContext(), "Error deleting account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                }
//                .addOnFailureListener {
//                    // Handle Firestore deletion failure
//                    Toast.makeText(requireContext(), "Error deleting user data", Toast.LENGTH_SHORT).show()
//                }
//        } else {
//            Toast.makeText(requireContext(), "No user is signed in", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun navigateToLogin() {
        // Redirect to the login screen (you may need to adjust based on your navigation setup)
        val intent = Intent(requireContext(), AuthActivity::class.java)
        startActivity(intent)
        requireActivity().finish()  // Close the current activity
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}