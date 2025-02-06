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
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.example.feather.viewmodels.DreamViewModel
import com.example.feather.viewmodels.details.DreamDetailViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DreamDetailFragment : Fragment() {

    private var _binding: FragmentDreamDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var dreamCategoryTextView: TextView
    private lateinit var hoursSleptTextView: TextView
    private lateinit var recurringCheckBox: CheckBox
    private lateinit var dreamTextView: TextView
    private lateinit var keywordsTextView: TextView

    private val dreamDetailViewModel : DreamDetailViewModel by viewModels()

    private var hours = 7
    private var minutes = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDreamDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dreamId = arguments?.getString("dreamId", "") ?: ""

        if (dreamId.isNotBlank()) {
            dreamDetailViewModel.getDreamById(dreamId)

            dreamDetailViewModel.dream.observe(viewLifecycleOwner) { selectedDream ->
                if (selectedDream != null) {
                    displayDreamDetails(selectedDream)
                } else {
                    Log.d("Error", "Dream not found for ID: $dreamId")
                    Toast.makeText(context, "Dream not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid dreamId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayDreamDetails(selectedDream: DreamModel) {
        dreamCategoryTextView = binding.dreamCategoryTextView
        hoursSleptTextView = binding.hoursSleptTextView
        recurringCheckBox = binding.recurringCheckBox
        dreamTextView = binding.dreamTextView
        keywordsTextView = binding.keywordsTextView

        binding.HomeTitleTextView.text = selectedDream.title

        dreamTextView.text = selectedDream.description
        hoursSleptTextView.text = selectedDream.hoursSlept
        recurringCheckBox.isChecked = selectedDream.isRecurring

        if(selectedDream.keywords.isEmpty()){
            keywordsTextView.text = "Keywords: none"
        }else{
            keywordsTextView.text = "Keywords: ${selectedDream.keywords}"
        }
        dreamCategoryTextView.text = "Category: ${selectedDream.category}"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}