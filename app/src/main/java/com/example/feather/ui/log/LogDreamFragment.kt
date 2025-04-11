package com.example.feather.ui.log

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentLogDreamBinding
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.example.feather.models.SymbolModel
import com.example.feather.viewmodels.DreamViewModel
import com.example.feather.viewmodels.ExploreViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import kotlin.math.exp

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

    //for speech-to-text:
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechIntent: Intent

    private val dreamViewModel : DreamViewModel by viewModels()
    private val exploreViewModel : ExploreViewModel by viewModels()

    private val selectedKeywords = mutableListOf<KeywordModel>()
    private val selectedSymbols = mutableListOf<SymbolModel>()

    private val speechTimeoutHandler = Handler(Looper.getMainLooper())
    private val speechTimeoutRunnable = Runnable {
        speechRecognizer.stopListening()
        Toast.makeText(requireContext(), "No speech detected. Try again.", Toast.LENGTH_SHORT).show()
    }


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

    private val requestAudioPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Permission granted! Tap the mic again.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Microphone permission is required for speech input.", Toast.LENGTH_LONG).show()
        }
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

        val micButton = view.findViewById<ImageButton>(R.id.micButton)

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

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                matches?.let {
                    val spokenText = it[0]
                    dreamInputEditText.append("$spokenText ")
                }
            }

            override fun onError(error: Int) {
                Toast.makeText(context, "Speech error: $error", Toast.LENGTH_SHORT).show()
            }

            // Optional: Handle other states
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {
                speechTimeoutHandler.removeCallbacks(speechTimeoutRunnable)
            }
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        micButton.setOnClickListener {
            if (checkAudioPermission()) {
                speechRecognizer.startListening(speechIntent)
                // Start the 5-second timeout
                speechTimeoutHandler.postDelayed(speechTimeoutRunnable, 7000)
            } else {
                requestAudioPermission()
            }
        }

        keywordSelectionButton.setOnClickListener {
            showKeywordSelectionDialog()
        }

        binding.selectSymbolsButton.setOnClickListener {
            showSymbolSelectionDialog()
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

    private fun showSymbolSelectionDialog() {
        val symbolList = mutableListOf<SymbolModel>()
        val selectedSymbolNames = selectedSymbols.map { it.name }.toMutableSet()

        val dialogView = layoutInflater.inflate(R.layout.dialog_select_symbols, null)
        val symbolListView = dialogView.findViewById<ListView>(R.id.symbolListView)
        val emptySymbolTextView = dialogView.findViewById<TextView>(R.id.emptySymbolTextView)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        symbolListView.adapter = adapter


        exploreViewModel.getSymbols()

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Symbols")
            .setView(dialogView)
            .setPositiveButton("Done", null)
            .setNegativeButton("Cancel", null)
            .create()

        exploreViewModel.symbols.observe(viewLifecycleOwner) { symbols ->
            //Log.d("keyword vm", keywords.toString())
            symbolList.clear()
            symbolList.addAll(symbols)

            adapter.clear()
            adapter.addAll(symbols.map { it.name })
            adapter.notifyDataSetChanged()

            if (symbols.isEmpty()) {
                symbolListView.visibility = View.GONE
                emptySymbolTextView.visibility = View.VISIBLE

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.visibility = View.GONE
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.text = "OK"
            } else {
                symbolListView.visibility = View.VISIBLE
                emptySymbolTextView.visibility = View.GONE
            }

            for (i in symbolList.indices) {
                if (selectedSymbolNames.contains(symbolList[i].name)) {
                    symbolListView.setItemChecked(i, true)
                }
            }
        }

        alertDialog.setOnShowListener {
            val doneButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            doneButton.setOnClickListener {
                selectedSymbols.clear()
                for (i in 0 until symbolListView.count) {
                    if (symbolListView.isItemChecked(i)) {
                        selectedSymbols.add(symbolList[i])
                    }
                }
                alertDialog.dismiss() // Ensure it closes immediately
            }
        }

        alertDialog.show()
    }

    private fun showKeywordSelectionDialog() {
        val keywordList = mutableListOf<KeywordModel>()
        //val selectedKeywordNames = mutableSetOf<String>()
        val selectedKeywordNames = selectedKeywords.map { it.name }.toMutableSet() // Track selections by name

        val dialogView = layoutInflater.inflate(R.layout.dialog_select_keywords, null)
        val keywordListView = dialogView.findViewById<ListView>(R.id.keywordListView)
        val emptyKeywordTextView = dialogView.findViewById<TextView>(R.id.emptyKeywordTextView)

        val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_multiple_choice)
        keywordListView.adapter = adapter


        dreamViewModel.getUserKeywords()

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Keywords")
            .setView(dialogView)
            .setPositiveButton("Done", null) // Set null here to override later
            .setNegativeButton("Cancel", null)
            .create()

        dreamViewModel.userKeywords.observe(viewLifecycleOwner) { keywords ->
            //Log.d("keyword vm", keywords.toString())
            keywordList.clear()
            keywordList.addAll(keywords)

            adapter.clear()
            adapter.addAll(keywords.map { it.name })
            adapter.notifyDataSetChanged()

            if (keywords.isEmpty()) {
                keywordListView.visibility = View.GONE
                emptyKeywordTextView.visibility = View.VISIBLE

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.visibility = View.GONE
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.text = "OK"
            } else {
                keywordListView.visibility = View.VISIBLE
                emptyKeywordTextView.visibility = View.GONE
            }

            for (i in keywordList.indices) {
                if (selectedKeywordNames.contains(keywordList[i].name)) {
                    keywordListView.setItemChecked(i, true)
                }
            }
        }

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

        emptyKeywordTextView.setOnClickListener {
            alertDialog.dismiss()
            // Navigate to add keyword screen
            findNavController().navigate(R.id.action_logDreamFragment_to_myKeywordsFragment)
        }

        alertDialog.show()
    }

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
            keywords = selectedKeywords.map { it.name },
            symbols = selectedSymbols.map { it.name }
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

    private fun checkAudioPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestAudioPermission() {
        requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        speechRecognizer.destroy()
        speechTimeoutHandler.removeCallbacks(speechTimeoutRunnable)
        _binding = null // Clear binding to prevent memory leaks
    }
}