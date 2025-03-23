package com.example.feather.ui.dreamanalysis

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.databinding.FragmentDreamanalysisBinding
import com.example.feather.viewmodels.ai.AIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DreamAnalysisFragment : Fragment() {

    private var _binding: FragmentDreamanalysisBinding? = null
    private val binding get() = _binding!!

    private val aiViewModel : AIViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDreamanalysisBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Dream analysis with Gemini AI"

        binding.analyzeOneDreamTextView.setOnClickListener {
            findNavController().navigate(R.id.action_dreamAnalysisFragment_to_selectDreamFragment)
        }

        aiViewModel.analysisResultWeekly.observe(viewLifecycleOwner) { result ->
            result?.let { analysis ->
                navigateToWeeklyAnalysisFragment(analysis)
            }
        }

        aiViewModel.analysisResultMonthly.observe(viewLifecycleOwner) { result ->
            result?.let { analysis ->
                navigateToMonthlyAnalysisFragment(analysis)
            }
        }

        binding.weeklyAnalysisTextView.setOnClickListener {
            aiViewModel.analyzeWeeklyDreams()
        }

        binding.monthlyAnalysisTextView.setOnClickListener {
            aiViewModel.analyzeMonthlyDreams()
        }
    }

    private fun navigateToWeeklyAnalysisFragment(analysisResult: String) {
        val bundle = Bundle().apply {
            putString("analysis_result", analysisResult)
        }
        aiViewModel.saveAnalysis(analysisResult, "weekly_interpretations")
        findNavController().navigate(
            R.id.action_dreamAnalysisFragment_to_analyzeWeeklyDreamsFragment,
            bundle
        )
    }

    private fun navigateToMonthlyAnalysisFragment(analysisResult: String) {
        val bundle = Bundle().apply {
            putString("analysis_result", analysisResult)
        }
        aiViewModel.saveAnalysis(analysisResult, "monthly_interpretations")
        findNavController().navigate(
            R.id.action_dreamAnalysisFragment_to_analyzeMonthlyDreamsFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}