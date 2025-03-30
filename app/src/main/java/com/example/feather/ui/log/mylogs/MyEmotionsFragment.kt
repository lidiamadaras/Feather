package com.example.feather.ui.log.mylogs

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentMyEmotionsBinding
import com.example.feather.models.EmotionModel
import com.example.feather.ui.adapter.EmotionsAdapter
import com.example.feather.viewmodels.FeelingViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyEmotionsFragment : Fragment() {

    private var _binding: FragmentMyEmotionsBinding? = null
    private val binding get() = _binding!!

    private val feelingViewModel : FeelingViewModel by viewModels()

    private lateinit var adapter: EmotionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyEmotionsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My emotions"

        feelingViewModel.getUserEmotions()

        adapter = EmotionsAdapter(
            listOf(),
            onItemClick = { emotion ->
                //navigateToEmotionDetail(emotion.name)
            },
            onItemLongClick = { emotion ->
                showDeleteConfirmationDialog(emotion)
            }
        )

        binding.fabAddEmotion.setOnClickListener{
            addEmotion()
        }

        // Set up the RecyclerView
        binding.emotionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.emotionsRecyclerView.adapter = adapter

        binding.emotionsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        feelingViewModel.userEmotions.observe(viewLifecycleOwner) { emotions ->
            if (!emotions.isNullOrEmpty()) {
                adapter.updateEmotions(emotions)
            } else {
                Toast.makeText(context, "No emotions available", Toast.LENGTH_SHORT).show()
            }
        }

        feelingViewModel.deleteEmotionResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Emotion deleted", Toast.LENGTH_SHORT).show()
                feelingViewModel.getUserEmotions()              //immediately refresh list
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error deleting emotion: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        feelingViewModel.saveEmotionResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Emotion saved", Toast.LENGTH_SHORT).show()
                feelingViewModel.getUserEmotions()
                //adapter.notifyDataSetChanged()
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error saving emotion: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

//    private fun navigateToEmotionDetail(id: String) {
//        val bundle = Bundle().apply {
//            putString("emotionId", id) // Pass only the recipe ID
//        }
//        findNavController().navigate(
//            R.id.action_myEmotionsFragment_to_emotionDetailFragment,
//            bundle
//        )
//    }

    private fun addEmotion() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_emotion, null)
        val emotionNameEditText = dialogView.findViewById<EditText>(R.id.emotionEditText)
        val emotionDescriptionEditText = dialogView.findViewById<EditText>(R.id.emotionDescEditText)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Add New Emotion")
            .setView(dialogView)
            .setPositiveButton("Save Emotion", null) // Overriding later
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.setOnShowListener {
            val saveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            saveButton.setOnClickListener {
                val name = emotionNameEditText.text.toString().trim()
                val description = emotionDescriptionEditText.text.toString().trim()

                if (name.isEmpty()) {
                    emotionNameEditText.error = "Emotion name cannot be empty"
                    return@setOnClickListener
                }

                // Check if the emotion already exists before saving
                feelingViewModel.checkIfEmotionExists(name) { exists ->
                    if (exists) {
                        emotionNameEditText.error = "Emotion already exists!"
                    } else {
                        val newEmotion = EmotionModel(
                            name = name,
                            dateAdded = Timestamp.now(),
                            description = description.ifEmpty { null }
                        )

                        feelingViewModel.saveEmotion(newEmotion)
                        alertDialog.dismiss()
                    }
                }
            }
        }

        alertDialog.show()
    }


    private fun showDeleteConfirmationDialog(emotion: EmotionModel) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Delete Emotion")
            .setMessage("Are you sure you want to proceed?")
            .setPositiveButton("Yes, delete") { dialog, _ ->
                feelingViewModel.deleteEmotion(emotion.name)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}