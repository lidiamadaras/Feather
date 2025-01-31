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
import android.widget.PopupMenu
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentHomeBinding
import com.example.feather.databinding.FragmentLogDreamBinding
import com.example.feather.models.Dream
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class LogDreamFragment : Fragment() {

    private var _binding: FragmentLogDreamBinding? = null
    private val binding get() = _binding!!

    private lateinit var dreamCategorySpinner: Spinner
    private lateinit var hoursSleptTextView: TextView
    private lateinit var recurringCheckBox: CheckBox
    private lateinit var dreamInputEditText: EditText
    private lateinit var saveDreamButton: Button
    private lateinit var dreamTitleEditText: EditText



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
    }

    private fun saveDream() {
        val dreamText = dreamInputEditText.text.toString().trim()
        val category = dreamCategorySpinner.selectedItem.toString()
        val isRecurring = recurringCheckBox.isChecked
        val title = dreamTitleEditText.text.toString().trim()


        // Validation
        if (dreamText.isEmpty()) {
            Toast.makeText(requireContext(), "Please describe your dream", Toast.LENGTH_SHORT).show()
            return
        }

        if (title.isEmpty()) {
            Toast.makeText(requireContext(), "Please add a title for your dream", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {

            val dream = Dream(
                dateAdded = Timestamp.now(), // Automatically get the current timestamp
                description = dreamText,
                category = category,
                hoursSlept = hoursSleptTextView.text.toString(),
                isRecurring = isRecurring,
                title = title
            )

            val db = FirebaseFirestore.getInstance()
            db.collection("users")
                .document(currentUser.uid)
                .collection("dreams")
                .add(dream)
                .addOnSuccessListener {
                    // Successfully saved the dream
                    Toast.makeText(requireContext(), "Dream saved!", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
                .addOnFailureListener { e ->
                    // Handle error
                    Toast.makeText(
                        requireContext(),
                        "Error saving dream: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
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