package com.example.feather.ui.ai.analyze_one_dream

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
import com.example.feather.databinding.FragmentAnalyzeDreamBinding
import com.example.feather.viewmodels.ai.AIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AnalyzeDreamFragment : Fragment() {

    private var _binding: FragmentAnalyzeDreamBinding? = null
    private val binding get() = _binding!!

    //private val aiViewModel : AIViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalyzeDreamBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val analysisResult = arguments?.getString("analysis_result") ?: "No analysis available"

        binding.analyzeOneDreamTextView.text = analysisResult
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}