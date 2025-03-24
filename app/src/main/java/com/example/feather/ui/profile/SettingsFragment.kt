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
import com.example.feather.databinding.FragmentSettingsBinding
import com.example.feather.viewmodels.ai.ApiKeyViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()
    private var isEditing = false   //for changing api key

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Settings"

        binding.etApiKey.isEnabled = false

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}