package com.example.feather.ui.log.mylogs.details

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentInterpretationDetailBinding
import com.example.feather.models.DreamInterpretationModel
import com.example.feather.viewmodels.ai.AIViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InterpretationDetailFragment : Fragment() {

    private var _binding: FragmentInterpretationDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var personaTextView: TextView
    private lateinit var analysisTextView: TextView

    private val aiViewModel : AIViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInterpretationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = arguments?.getString("id", "") ?: ""
        val type = arguments?.getString("type", "") ?: ""

        if (id.isNotBlank()) {
            aiViewModel.getInterpretationById(id, type)

            aiViewModel.interpretation.observe(viewLifecycleOwner) { interpretation ->
                if (interpretation != null) {
                    displayDetails(interpretation)
                } else {
                    Log.d("Error", "Interpretation not found for ID: $id")
                    Toast.makeText(context, "Interpretation not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid id", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayDetails(interpretation: DreamInterpretationModel) {
        personaTextView = binding.personaTextView
        analysisTextView = binding.analysisTextView

        binding.HomeTitleTextView.text = interpretation.title

        analysisTextView.text = interpretation.analysisText

        personaTextView.text = "Persona used: ${interpretation.personaGemini}"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}