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


    //for hours slept selector:
    private var hours = 0
    private var minutes = 0

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
            showTimePickerDialog()
        }

        timeEndedTextView.setOnClickListener {
            showTimePickerDialog()
        }

        updateTimeStartedText()


        binding.HomeTitleTextView.text = "Log a feeling"

        emotionSelectionButton.setOnClickListener {
            showEmotionSelectionDialog()
        }

        feelingViewModel.getUserEmotions()

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

        feelingViewModel.saveEmotionResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Emotion saved", Toast.LENGTH_SHORT).show()
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error saving emotion: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun showEmotionSelectionDialog() {
        val emotionList = mutableListOf<EmotionModel>()

        val dialogView = layoutInflater.inflate(R.layout.dialog_select_emotion, null)
        val emotionListView = dialogView.findViewById<ListView>(R.id.emotionListView)
        val addEmotionButton = dialogView.findViewById<Button>(R.id.addEmotionButton)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        emotionListView.adapter = adapter

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Emotion")
            .setView(dialogView)
            .setPositiveButton("Done") { _, _ ->
                selectedEmotion.clear()
                for (i in 0 until emotionListView.count) {
                    if (emotionListView.isItemChecked(i)) {
                        selectedEmotion.add(emotionList[i])
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .create()


        feelingViewModel.getUserEmotions()

        feelingViewModel.userEmotions.observe(viewLifecycleOwner) { emotions ->
            emotionList.clear()
            emotionList.addAll(emotions)
            adapter.clear()
            adapter.addAll(emotions.map { it.name })
            adapter.notifyDataSetChanged()
        }

        feelingViewModel.saveEmotionResult.observe(viewLifecycleOwner) { result ->
            if( result.isSuccess){
                feelingViewModel.userEmotions.observe(viewLifecycleOwner) { emotions ->
                    emotionList.clear()
                    emotionList.addAll(emotions)
                    adapter.clear()
                    adapter.addAll(emotions.map { it.name })
                    adapter.notifyDataSetChanged()
                }
            }
        }



        //feelingViewModel.getUserEmotions()

        addEmotionButton.setOnClickListener {
            showAddEmotionDialog()
        }

        alertDialog.show()
    }

    private fun showAddEmotionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_emotion, null)
        val emotionEditText = dialogView.findViewById<EditText>(R.id.emotionEditText)
        val emotionDescEditText = dialogView.findViewById<EditText>(R.id.emotionDescEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Emotion")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val emotionName = emotionEditText.text.toString().trim()
                val emotionDescription = emotionDescEditText.text.toString().trim()
                val newEmotion = EmotionModel(name = emotionName, dateAdded = Timestamp.now(), description = emotionDescription)
                feelingViewModel.saveEmotion(newEmotion)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }



    private fun saveFeeling() {
        val intensity = intensitySpinner.selectedItem.toString()



        if (selectedEmotion.isNotEmpty()) {
            firstEmotion = selectedEmotion.first()
        } else {
            Toast.makeText(requireContext(), "Please select an emotion first", Toast.LENGTH_SHORT).show()
            return
        }

        val feeling = FeelingModel(
            dateAdded = Timestamp.now(), // Automatically get the current timestamp
            timeStarted = timeStartedTextView.text.toString(),
            timeEnded = timeEndedTextView.text.toString(),
            intensity = intensity,
            emotion = firstEmotion.name
        )


        feelingViewModel.saveFeeling(feeling)
    }


    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                hours = selectedHour
                minutes = selectedMinute
                updateTimeStartedText()
            },
            hour,
            minute,
            true // Use 24-hour format
        )
        timePickerDialog.show()
    }

    private fun updateTimeStartedText() {

        if (hours == 0 && minutes == 0) {
            val calendar = Calendar.getInstance()
            hours = calendar.get(Calendar.HOUR_OF_DAY)
            minutes = calendar.get(Calendar.MINUTE)
        }

        val formattedTime = String.format("%d:%02d", hours, minutes)
        val formattedTime2 = String.format("%d:%02d",--hours, minutes)

        timeStartedTextView.text = formattedTime2
        timeEndedTextView.text = formattedTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}