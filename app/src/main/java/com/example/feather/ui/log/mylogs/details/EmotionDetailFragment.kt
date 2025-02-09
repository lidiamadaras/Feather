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
import com.example.feather.databinding.FragmentEmotionDetailBinding
import com.example.feather.models.EmotionModel
import com.example.feather.viewmodels.details.EmotionDetailViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EmotionDetailFragment : Fragment() {

    private var _binding: FragmentEmotionDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var emotionTextView: TextView
    private lateinit var descriptionTextView: TextView


    private val emotionDetailViewModel : EmotionDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmotionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val emotionId = arguments?.getString("emotionId", "") ?: ""

        if (emotionId.isNotBlank()) {
            emotionDetailViewModel.getEmotionById(emotionId)

            emotionDetailViewModel.emotion.observe(viewLifecycleOwner) { selectedEmotion ->
                if (selectedEmotion != null) {
                    displayEmotionDetails(selectedEmotion)
                } else {
                    Log.d("Error", "Emotion not found for ID: $emotionId")
                    Toast.makeText(context, "Emotion not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid emotionId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayEmotionDetails(selectedEmotion: EmotionModel) {
        emotionTextView = binding.emotionTextView
        descriptionTextView = binding.descriptionTextView


        binding.HomeTitleTextView.text = "Emotion"

        emotionTextView.text = selectedEmotion.name
        descriptionTextView.text = selectedEmotion.description
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}