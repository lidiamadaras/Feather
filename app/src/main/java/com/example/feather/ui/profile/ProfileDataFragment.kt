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
import com.example.feather.databinding.FragmentProfileDataBinding
import com.example.feather.models.UserData
import com.example.feather.viewmodels.ai.ApiKeyViewModel
import com.example.feather.viewmodels.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDataFragment : Fragment() {

    private var _binding: FragmentProfileDataBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()            //need it for clearing key on sign out
    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileDataBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Profile Data"


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

        //profile data/info:

        authViewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.firstNameEditText.setText(user.firstName)
                binding.lastNameEditText.setText(user.lastName)
                binding.dobEditText.setText(user.dateOfBirth)
                binding.emailEditText.setText(user.email)
            }
        }

        authViewModel.updateStatus.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save changes", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.getUserData()

        binding.editAccountButton.setOnClickListener {
            toggleEditing()
        }
    }

    private fun toggleEditing() {
        isEditing = !isEditing
        // Enable or disable the EditTexts based on isEditing
        binding.firstNameEditText.isEnabled = isEditing
        binding.lastNameEditText.isEnabled = isEditing
        binding.dobEditText.isEnabled = isEditing
        binding.emailEditText.isEnabled = isEditing

        // Update the button text
        if (isEditing) {
            binding.editAccountButton.text = "Save"
        } else {
            binding.editAccountButton.text = "Edit Account Data"
        }

        if (!isEditing) {
            val updatedUserData = UserData(
                firstName = binding.firstNameEditText.text.toString(),
                lastName = binding.lastNameEditText.text.toString(),
                dateOfBirth = binding.dobEditText.text.toString(),
                email = binding.emailEditText.text.toString()
            )
            authViewModel.updateUserData(updatedUserData)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}