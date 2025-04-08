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
    private var personaGemini: String? = null

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

        aiViewModel.loadPreferredPersona()

        aiViewModel.preferredPersona.observe(viewLifecycleOwner) { result ->
            result.onSuccess { persona ->
                personaGemini = persona
                Log.d("Persona", "Loaded preferred persona: $personaGemini")
            }

            result.onFailure {
                Log.e("Persona", "Failed to load preferred persona: ${it.message}")
                personaGemini = null
            }
        }


        aiViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            binding.weeklyAnalysisTextView.isEnabled = !isLoading
            binding.monthlyAnalysisTextView.isEnabled = !isLoading
            binding.analyzeOneDreamTextView.isEnabled = !isLoading
        }

        binding.analyzeOneDreamTextView.setOnClickListener {
            findNavController().navigate(R.id.action_dreamAnalysisFragment_to_selectDreamFragment)
        }

        binding.generateImageTextView.setOnClickListener {
            findNavController().navigate(R.id.action_dreamAnalysisFragment_to_selectDreamImageGenerationFragment)
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

    fun getPromptFromPersona(persona: String): String {
        return when (persona) {
            "Christian AI" -> """
            Imagine you are a compassionate Christian priest or counselor. Analyze the following dream using only Christian faith-based principles, biblical symbolism, and spiritual insights. Reference scripture when appropriate. Offer a gentle, faith-centered interpretation that encourages spiritual growth, reflection, and hope. Avoid psychological or secular interpretations.
        """.trimIndent()

            "Psychological AI" -> """
            Imagine you are a trained Freudian psychologist. Analyze the following dream using psychological principles and dream symbolism. Focus on unconscious desires, archetypes, emotions, and personal experiences. Use therapeutic language to guide the user toward deeper self-awareness and understanding.
        """.trimIndent()

            else -> ""
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