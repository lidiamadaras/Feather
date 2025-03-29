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
import com.example.feather.databinding.FragmentDreamDetailBinding
import com.example.feather.databinding.FragmentFeelingDetailBinding
import com.example.feather.models.DreamModel
import com.example.feather.models.FeelingModel
import com.example.feather.models.KeywordModel
import com.example.feather.viewmodels.DreamViewModel
import com.example.feather.viewmodels.details.DreamDetailViewModel
import com.example.feather.viewmodels.details.FeelingDetailViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeelingDetailFragment : Fragment() {

    private var _binding: FragmentFeelingDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var feelingIntensityTextView: TextView
    private lateinit var timeStartedTextView: TextView
    private lateinit var timeEndedTextView: TextView
    private lateinit var emotionTextView: TextView

    private val feelingDetailViewModel : FeelingDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFeelingDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val feelingId = arguments?.getString("feelingId", "") ?: ""

        if (feelingId.isNotBlank()) {
            feelingDetailViewModel.getFeelingById(feelingId)

            feelingDetailViewModel.feeling.observe(viewLifecycleOwner) { selectedFeeling ->
                if (selectedFeeling != null) {
                    displayFeelingDetails(selectedFeeling)
                } else {
                    Log.d("Error", "Feeling not found for ID: $feelingId")
                    Toast.makeText(context, "Feeling not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid feelingId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayFeelingDetails(selectedFeeling: FeelingModel) {
        feelingIntensityTextView = binding.feelingIntensityTextView
        emotionTextView = binding.emotionTextView

        emotionTextView.text = selectedFeeling.emotion
        timeStartedTextView.text = selectedFeeling.timeStarted
        timeEndedTextView.text = selectedFeeling.timeEnded
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}