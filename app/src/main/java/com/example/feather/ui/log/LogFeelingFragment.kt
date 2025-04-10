package com.example.feather.ui.log

import android.app.AlertDialog
import android.app.TimePickerDialog
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
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentLogFeelingBinding
import com.example.feather.models.EmotionModel
import com.example.feather.models.FeelingModel
import com.example.feather.models.KeywordModel
import com.example.feather.viewmodels.FeelingViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class LogFeelingFragment : Fragment() {

    private var _binding: FragmentLogFeelingBinding? = null
    private val binding get() = _binding!!

    private lateinit var intensitySpinner: Spinner
    private lateinit var timeStartedTextView: TextView
    private lateinit var timeEndedTextView: TextView
    private lateinit var saveFeelingButton: Button
    private lateinit var emotionSelectionButton: Button

    private val feelingViewModel : FeelingViewModel by viewModels()

    private var selectedEmotion = mutableListOf<EmotionModel>()
    private var firstEmotion = EmotionModel()

    private val selectedKeywords = mutableListOf<KeywordModel>()


    //for hours slept selector:
    private var startHour = 0
    private var startMinute = 0
    private var endHour = 0
    private var endMinute = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogFeelingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intensitySpinner = binding.intensitySpinner
        timeStartedTextView = binding.timeStartedTextView
        timeEndedTextView = binding.timeEndedTextView

        saveFeelingButton = binding.saveFeelingButton

        emotionSelectionButton = binding.selectEmotionButton

        timeStartedTextView.setOnClickListener {
            showTimePickerDialog(isStartTime = true)
        }

        timeEndedTextView.setOnClickListener {
            showTimePickerDialog(isStartTime = false)
        }


        binding.HomeTitleTextView.text = "Log a feeling"

        emotionSelectionButton.setOnClickListener {
            showEmotionSelectionDialog()
        }

        //feelingViewModel.getUserEmotions()

        saveFeelingButton.setOnClickListener {
            saveFeeling()
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.feeling_intensities,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            intensitySpinner.adapter = adapter
        }

        feelingViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Feeling saved successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Navigate back after saving
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error saving feeling: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEmotionSelectionDialog() {
        val emotionList = mutableListOf<EmotionModel>()
        var selectedEmotionName: String? = selectedEmotion.firstOrNull()?.name // Track selected emotion by name


        val dialogView = layoutInflater.inflate(R.layout.dialog_select_emotion, null)
        val emotionListView = dialogView.findViewById<ListView>(R.id.emotionListView)
        val emptyEmotionTextView = dialogView.findViewById<TextView>(R.id.emptyEmotionTextView)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_single_choice)
        emotionListView.adapter = adapter
        emotionListView.choiceMode = ListView.CHOICE_MODE_SINGLE // Ensure only one selection

        feelingViewModel.getUserEmotions()

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Emotion")
            .setView(dialogView)
            .setPositiveButton("Done", null) // Override later
            .setNegativeButton("Cancel", null)
            .create()

        feelingViewModel.userEmotions.observe(viewLifecycleOwner) { emotions ->
            emotionList.clear()
            emotionList.addAll(emotions)

            adapter.clear()
            adapter.addAll(emotions.map { it.name })
            adapter.notifyDataSetChanged()

            if (emotions.isEmpty()) {
                emotionListView.visibility = View.GONE
                emptyEmotionTextView.visibility = View.VISIBLE

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.visibility = View.GONE
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.text = "OK"
            } else {
                emotionListView.visibility = View.VISIBLE
                emptyEmotionTextView.visibility = View.GONE
            }

            // Restore checked state if an emotion was previously selected
            val selectedIndex = emotionList.indexOfFirst { it.name == selectedEmotionName }
            if (selectedIndex != -1) {
                emotionListView.setItemChecked(selectedIndex, true)
            }
        }

        alertDialog.setOnShowListener {
            val doneButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            doneButton.setOnClickListener {
                val checkedPosition = emotionListView.checkedItemPosition
                if (checkedPosition != ListView.INVALID_POSITION) {
                    selectedEmotion.clear()
                    selectedEmotion.add(emotionList[checkedPosition]) // Update selectedEmotion list
                    selectedEmotionName = emotionList[checkedPosition].name // Store selected name
                }
                alertDialog.dismiss()
            }
        }

        emptyEmotionTextView.setOnClickListener {
            alertDialog.dismiss()
            // Navigate to add keyword screen
            findNavController().navigate(R.id.action_logFeelingFragment_to_myEmotionsFragment)
        }

        alertDialog.show()
    }

    private fun saveFeeling() {
        val intensity = intensitySpinner.selectedItem.toString()

        if (selectedEmotion.isNotEmpty()) {
            firstEmotion = selectedEmotion.first()
        } else {
            Toast.makeText(requireContext(), "Please select an emotion first", Toast.LENGTH_SHORT).show()
            return
        }

        val startTotalMinutes = startHour * 60 + startMinute
        val endTotalMinutes = endHour * 60 + endMinute

        if (endTotalMinutes <= startTotalMinutes) {
            Toast.makeText(requireContext(), "End time must be after start time", Toast.LENGTH_SHORT).show()
            return
        }

        val feeling = FeelingModel(
            dateAdded = Timestamp.now(), // Automatically get the current timestamp
            timeStarted = String.format("%02d:%02d", startHour, startMinute),
            timeEnded = String.format("%02d:%02d", endHour, endMinute),
            intensity = intensity,
            emotion = firstEmotion.name
        )

        feelingViewModel.saveFeeling(feeling)
    }


    private fun showTimePickerDialog(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                if (isStartTime) {
                    startHour = selectedHour
                    startMinute = selectedMinute

                    // If end time is still default, set it to 1 hour later
                    if (endHour == 0 && endMinute == 0) {
                        val endCalendar = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, startHour)
                            set(Calendar.MINUTE, startMinute)
                            add(Calendar.HOUR_OF_DAY, 1)
                        }
                        endHour = endCalendar.get(Calendar.HOUR_OF_DAY)
                        endMinute = endCalendar.get(Calendar.MINUTE)
                    }
                } else {
                    endHour = selectedHour
                    endMinute = selectedMinute
                }

                updateDisplayedTimes()
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }


    private fun updateDisplayedTimes() {
        timeStartedTextView.text = String.format("%02d:%02d", startHour, startMinute)
        timeEndedTextView.text = String.format("%02d:%02d", endHour, endMinute)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}