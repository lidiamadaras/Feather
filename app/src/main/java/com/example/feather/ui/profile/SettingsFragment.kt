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
import com.example.feather.R
import com.example.feather.databinding.FragmentSettingsBinding
import com.example.feather.models.UserData
import com.example.feather.viewmodels.ai.AIViewModel
import com.example.feather.viewmodels.ai.ApiKeyViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val apiKeyViewModel: ApiKeyViewModel by viewModels()
    private val aiViewModel: AIViewModel by viewModels()

    private var isEditing = false   //for changing api key
    private var isEditingPersona = false   //for changing persona preference

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
        binding.radioGroupPersonas.isEnabled = false
        binding.radioPersona1.isEnabled = false
        binding.radioPersona2.isEnabled = false
        //binding.radioPersona3.isEnabled = false

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
        binding.btnEditApiKey.setOnClickListener {
            toggleEditing()
        }

        apiKeyViewModel.loadApiKey()

        // Personas:

        aiViewModel.loadPreferredPersona()

        binding.btnEditPersona.setOnClickListener {
            togglePersonaEditing()
        }

        aiViewModel.preferredPersona.observe(viewLifecycleOwner) { result ->
            result.onSuccess { persona ->
                when (persona) {
                    "Psychological" -> binding.radioPersona1.isChecked = true
                    "Christian" -> binding.radioPersona2.isChecked = true
                    //"Creative AI" -> binding.radioPersona3.isChecked = true
                }
            }
        }
    }

    private fun savePreferredPersona() {
        val selectedPersona = when (binding.radioGroupPersonas.checkedRadioButtonId) {
            R.id.radioPersona1 -> "Psychological"
            R.id.radioPersona2 -> "Christian"
            //R.id.radioPersona3 -> "Creative AI"
            else -> null
        }

        selectedPersona?.let {
            aiViewModel.savePreferredPersona(it)
            Toast.makeText(requireContext(), "AI Persona preference saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun togglePersonaEditing() {
        isEditingPersona = !isEditingPersona
        binding.radioGroupPersonas.isEnabled = isEditingPersona
        binding.radioPersona1.isEnabled = isEditingPersona
        binding.radioPersona2.isEnabled = isEditingPersona
        //binding.radioPersona3.isEnabled = isEditingPersona


        if (isEditingPersona) {
            binding.btnEditPersona.text = "Save preference"
        } else {
            binding.btnEditPersona.text = "Edit preference"
            savePreferredPersona()
        }
    }

    private fun toggleEditing() {
        isEditing = !isEditing
        // Enable or disable the EditTexts based on isEditing
        binding.etApiKey.isEnabled = isEditing

        // Update the button text
        if (isEditing) {
            binding.btnEditApiKey.text = "Save"
        } else {
            binding.btnEditApiKey.text = "Edit Account Data"
        }

        if (!isEditing) {
            val apiKey = binding.etApiKey.text.toString().trim()
            apiKeyViewModel.saveApiKey(apiKey)
            Toast.makeText(requireContext(), "API Key saved successfully!", Toast.LENGTH_SHORT).show()
            binding.etApiKey.isEnabled = false  // Disable after saving
            isEditing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}