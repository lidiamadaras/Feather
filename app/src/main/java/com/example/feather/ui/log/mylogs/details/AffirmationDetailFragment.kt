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
import com.example.feather.databinding.FragmentAffirmationDetailBinding
import com.example.feather.models.AffirmationModel
import com.example.feather.viewmodels.details.AffirmationDetailViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AffirmationDetailFragment : Fragment() {

    private var _binding: FragmentAffirmationDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var affirmationTextView: TextView

    private val affirmationDetailViewModel : AffirmationDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAffirmationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val affirmationId = arguments?.getString("affirmationId", "") ?: ""

        if (affirmationId.isNotBlank()) {
            affirmationDetailViewModel.getAffirmationById(affirmationId)

            affirmationDetailViewModel.affirmation.observe(viewLifecycleOwner) { selectedAffirmation ->
                if (selectedAffirmation != null) {
                    displayAffirmationDetails(selectedAffirmation)
                } else {
                    Log.d("Error", "Affirmation not found for ID: $affirmationId")
                    Toast.makeText(context, "Affirmation not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid affirmationId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayAffirmationDetails(selectedAffirmation: AffirmationModel) {
        affirmationTextView = binding.affirmationTextView

        binding.HomeTitleTextView.text = "Affirmation"

        affirmationTextView.text = selectedAffirmation.text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}