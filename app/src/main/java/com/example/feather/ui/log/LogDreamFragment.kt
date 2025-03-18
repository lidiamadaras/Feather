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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentLogDreamBinding
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.example.feather.viewmodels.DreamViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogDreamFragment : Fragment() {

    private var _binding: FragmentLogDreamBinding? = null
    private val binding get() = _binding!!

    private lateinit var dreamCategorySpinner: Spinner
    private lateinit var hoursSleptTextView: TextView
    private lateinit var recurringCheckBox: CheckBox
    private lateinit var dreamInputEditText: EditText
    private lateinit var saveDreamButton: Button
    private lateinit var dreamTitleEditText: EditText
    private lateinit var keywordSelectionButton: Button

    private val dreamViewModel : DreamViewModel by viewModels()

    private val selectedKeywords = mutableListOf<KeywordModel>()



    //for hours slept selector:
    private var hours = 7
    private var minutes = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogDreamBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dreamCategorySpinner = binding.dreamCategorySpinner
        hoursSleptTextView = binding.hoursSleptTextView
        recurringCheckBox = binding.recurringCheckBox
        dreamInputEditText = binding.dreamInputEditText
        saveDreamButton = binding.saveDreamButton
        dreamTitleEditText = binding.dreamTitleEditText

        val increaseButton = binding.increaseTimeButton
        val decreaseButton = binding.decreaseTimeButton
        keywordSelectionButton = binding.selectKeywordsButton

        updateHoursSleptText()

        increaseButton.setOnClickListener {
            if (hours < 20 || (hours == 20 && minutes == 0)) {
                increaseTime()
            }
        }

        decreaseButton.setOnClickListener {
            if (hours > 1 || (hours == 1 && minutes > 0)) {
                decreaseTime()
            }
        }

        binding.HomeTitleTextView.text = "Log a dream"

        keywordSelectionButton.setOnClickListener {
            showKeywordSelectionDialog()
        }


        saveDreamButton.setOnClickListener {
            saveDream()
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.dream_categories,
            android.R.layout.simple_spinner_dropdown_item
        ).also { adapter ->
            dreamCategorySpinner.adapter = adapter
        }

        dreamViewModel.saveKeywordResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Keyword saved", Toast.LENGTH_SHORT).show()
                //dreamViewModel.getUserKeywords()
                showKeywordSelectionDialog()

            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error saving keyword: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        dreamViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Dream saved successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp() // Navigate back after saving
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error saving dream: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }




    }

    private fun showKeywordSelectionDialog() {
        val keywordList = mutableListOf<KeywordModel>()
        //val selectedKeywordNames = mutableSetOf<String>()
        val selectedKeywordNames = selectedKeywords.map { it.name }.toMutableSet() // Track selections by name

        val dialogView = layoutInflater.inflate(R.layout.dialog_select_keywords, null)
        val keywordListView = dialogView.findViewById<ListView>(R.id.keywordListView)
        //val addKeywordButton = dialogView.findViewById<Button>(R.id.addKeywordButton)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        keywordListView.adapter = adapter


        dreamViewModel.getUserKeywords()

        dreamViewModel.userKeywords.observe(viewLifecycleOwner) { keywords ->
            //Log.d("keyword vm", keywords.toString())
            keywordList.clear()
            keywordList.addAll(keywords)

            adapter.clear()
            adapter.addAll(keywords.map { it.name })
            adapter.notifyDataSetChanged()

            // Restore checked state
            for (i in keywordList.indices) {
                if (selectedKeywordNames.contains(keywordList[i].name)) {
                    keywordListView.setItemChecked(i, true)
                }
            }
        }


        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Keywords")
            .setView(dialogView)
            .setPositiveButton("Done", null) // Set null here to override later
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.setOnShowListener {
            val doneButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            doneButton.setOnClickListener {
                selectedKeywords.clear()
                for (i in 0 until keywordListView.count) {
                    if (keywordListView.isItemChecked(i)) {
                        selectedKeywords.add(keywordList[i])
                    }
                }
                alertDialog.dismiss() // Ensure it closes immediately
            }
        }


//        val dialogViewAddKeyword = layoutInflater.inflate(R.layout.dialog_add_keyword, null)
//        val keywordEditText = dialogViewAddKeyword.findViewById<EditText>(R.id.keywordEditText)
//        var newKeyword = KeywordModel()
//
//        addKeywordButton.setOnClickListener {
//            val keywordName = keywordEditText.text.toString().trim()
//            if (keywordName.isNotEmpty()) {
//                newKeyword = KeywordModel(name = keywordName, dateAdded = Timestamp.now())
//            }
//            if (!keywordList.contains(newKeyword)) {
//                dreamViewModel.saveKeyword(newKeyword)  // Save new keyword
//                // Ensure it appears immediately in the list
//                keywordList.add(newKeyword)
//                adapter.notifyDataSetChanged()
//            } else {
//                Toast.makeText(requireContext(), "Keyword already exists or is empty", Toast.LENGTH_SHORT).show()
//            }
//        }

//        addKeywordButton.setOnClickListener {
//            //showAddKeywordDialog(alertDialog)
//        }

        alertDialog.show()
    }

//    private fun showAddKeywordDialog(parentDialog: AlertDialog) {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_add_keyword, null)
//        val keywordEditText = dialogView.findViewById<EditText>(R.id.keywordEditText)
//
//        AlertDialog.Builder(requireContext())
//            .setTitle("Add New Keyword")
//            .setView(dialogView)
//            .setPositiveButton("Save") { _, _ ->
//                val keywordName = keywordEditText.text.toString().trim()
//                if (keywordName.isNotEmpty()) {
//                    val newKeyword = KeywordModel(name = keywordName, dateAdded = Timestamp.now())
//
//                    // Save and refresh dialog
//                    dreamViewModel.saveKeyword(newKeyword)
//                    parentDialog.dismiss() // Close current dialog
//                    showKeywordSelectionDialog() // Reopen with updated list
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }

    private fun saveDream() {
        val dreamText = dreamInputEditText.text.toString().trim()
        val category = dreamCategorySpinner.selectedItem.toString()
        val isRecurring = recurringCheckBox.isChecked
        val title = dreamTitleEditText.text.toString().trim()


        val dream = DreamModel(
            dateAdded = Timestamp.now(), // Automatically get the current timestamp
            description = dreamText,
            category = category,
            hoursSlept = hoursSleptTextView.text.toString(),
            isRecurring = isRecurring,
            title = title,
            keywords = selectedKeywords.map { it.name }
        )

        Log.d("keywords", dream.keywords.toString())

        dreamViewModel.saveDream(dream)
    }


    //hours slept selector - helper functions
    private fun increaseTime() {
        if (minutes == 30) {
            hours++
            minutes = 0
        } else {
            minutes = 30
        }
        updateHoursSleptText()
    }

    private fun decreaseTime() {
        if (minutes == 0) {
            hours--
            minutes = 30
        } else {
            minutes = 0
        }
        updateHoursSleptText()
    }

    private fun updateHoursSleptText() {
        val formattedTime = String.format("%d:%02d", hours, minutes)
        hoursSleptTextView.text = formattedTime
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}