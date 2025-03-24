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
import com.example.feather.viewmodels.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var isEditing = false   //for changing api key

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Profile"

        binding.etApiKey.isEnabled = false

        authViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.signOutButton.setOnClickListener {
            apiKeyViewModel.clearApiKey()
            authViewModel.signOut()
            navigateToAuthScreen()
        }

        binding.deleteAccountButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        authViewModel.deleteAccountStatus.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                apiKeyViewModel.clearApiKey()
                navigateToAuthScreen()
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error deleting account: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }


        //api key:

        apiKeyViewModel.apiKey.observe(viewLifecycleOwner) { key ->
            binding.etApiKey.setText(key ?: "")
        }

        apiKeyViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEditApiKey.setOnClickListener {
            isEditing = true
            binding.etApiKey.isEnabled = true
            binding.etApiKey.requestFocus()
            binding.etApiKey.selectAll()
        }

        // Save API Key Button - Saves Key Only If Edited
        binding.btnSaveApiKey.setOnClickListener {
            if (isEditing) {
                val apiKey = binding.etApiKey.text.toString().trim()
                apiKeyViewModel.saveApiKey(apiKey)
                Toast.makeText(requireContext(), "API Key saved successfully!", Toast.LENGTH_SHORT).show()
                binding.etApiKey.isEnabled = false  // Disable after saving
                isEditing = false
            }
        }

        apiKeyViewModel.loadApiKey()
    }

    private fun navigateToAuthScreen() {
        startActivity(Intent(requireContext(), AuthActivity::class.java))
        activity?.finish()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Account")
            .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
            .setPositiveButton("Yes") { _, _ -> authViewModel.deleteAccount() }
            .setNegativeButton("No", null)
            .show()
    }

//    private fun navigateToLogin() {
//        // Redirect to the login screen (you may need to adjust based on your navigation setup)
//        val intent = Intent(requireContext(), AuthActivity::class.java)
//        startActivity(intent)
//        requireActivity().finish()  // Close the current activity
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}