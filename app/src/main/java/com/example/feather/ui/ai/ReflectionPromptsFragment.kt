package com.example.feather.ui.ai
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.databinding.FragmentAnalyzeMonthlyDreamsBinding
import com.example.feather.databinding.FragmentReflectionPromptsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReflectionPromptsFragment : Fragment() {

    private var _binding: FragmentReflectionPromptsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReflectionPromptsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val analysisResult = arguments?.getString("prompt_result") ?: "No answer available"

        binding.analyzeMonthlyDreamsTextView.text = analysisResult
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}