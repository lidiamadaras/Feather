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
import com.example.feather.databinding.FragmentReflectionBinding
import com.example.feather.models.ReflectionModel
import com.example.feather.viewmodels.ReflectionViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReflectionFragment : Fragment() {

    private var _binding: FragmentReflectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var reflectionEditText: EditText
    private lateinit var saveReflectionButton: Button

    private val reflectionViewModel : ReflectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReflectionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        reflectionEditText = binding.reflectionEditText
        saveReflectionButton = binding.saveReflectionButton

        binding.HomeTitleTextView.text = "Reflect on your day"

        saveReflectionButton.setOnClickListener {
            saveReflection()
        }

        reflectionViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Reflection saved successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Navigate back after saving
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error saving reflection: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveReflection() {
        val reflectionText = reflectionEditText.text.toString().trim()

        val reflection = ReflectionModel(
            dateAdded = Timestamp.now(), // Automatically get the current timestamp
            text = reflectionText
        )
        reflectionViewModel.saveReflection(reflection)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}