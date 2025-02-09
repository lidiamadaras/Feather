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
import com.example.feather.databinding.FragmentReflectionDetailBinding
import com.example.feather.models.ReflectionModel
import com.example.feather.viewmodels.details.ReflectionDetailViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReflectionDetailFragment : Fragment() {

    private var _binding: FragmentReflectionDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var reflectionTextView: TextView

    private val reflectionDetailViewModel : ReflectionDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReflectionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val reflectionId = arguments?.getString("reflectionId", "") ?: ""

        if (reflectionId.isNotBlank()) {
            reflectionDetailViewModel.getReflectionById(reflectionId)

            reflectionDetailViewModel.reflection.observe(viewLifecycleOwner) { selectedReflection ->
                if (selectedReflection != null) {
                    displayReflectionDetails(selectedReflection)
                } else {
                    Log.d("Error", "Reflection not found for ID: $reflectionId")
                    Toast.makeText(context, "Reflection not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid reflectionId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayReflectionDetails(selectedReflection: ReflectionModel) {
        reflectionTextView = binding.reflectionTextView

        binding.HomeTitleTextView.text = "Affirmation"

        reflectionTextView.text = selectedReflection.text
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}