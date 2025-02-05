package com.example.feather.ui.log

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.databinding.FragmentAffirmationBinding
import com.example.feather.models.AffirmationModel
import com.example.feather.viewmodels.AffirmationViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffirmationFragment : Fragment() {

    private var _binding: FragmentAffirmationBinding? = null
    private val binding get() = _binding!!

    private lateinit var affirmationEditText: EditText
    private lateinit var saveAffirmationButton: Button

    private val affirmationViewModel : AffirmationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAffirmationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        affirmationEditText = binding.affirmationEditText
        saveAffirmationButton = binding.saveAffirmationButton

        binding.HomeTitleTextView.text = "Set an affirmation"

        saveAffirmationButton.setOnClickListener {
            saveAffirmation()
        }

        affirmationViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Affirmation saved successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Navigate back after saving
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error saving affirmation: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAffirmation() {
        val affirmationText = affirmationEditText.text.toString().trim()

        val affirmation = AffirmationModel(
            dateAdded = Timestamp.now(), // Automatically get the current timestamp
            text = affirmationText
        )
        affirmationViewModel.saveAffirmation(affirmation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}