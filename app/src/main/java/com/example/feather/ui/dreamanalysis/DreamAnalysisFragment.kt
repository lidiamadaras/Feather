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
    private var personaGemini: String = ""
    private var prompt: String = ""

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
                if (persona != null) {
                    personaGemini = persona
                }
                Log.d("Persona", "Loaded preferred persona: $personaGemini")

                prompt = getPromptFromPersona(personaGemini)
            }

            result.onFailure {
                Log.e("Persona", "Failed to load preferred persona: ${it.message}")
                personaGemini = ""
            }
        }


        aiViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            binding.weeklyAnalysisTextView.isEnabled = !isLoading
            binding.monthlyAnalysisTextView.isEnabled = !isLoading
            binding.analyzeOneDreamTextView.isEnabled = !isLoading
        }

        binding.analyzeOneDreamTextView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("prompt", prompt)
            }
            findNavController().navigate(R.id.action_dreamAnalysisFragment_to_selectDreamFragment, bundle)
        }

        binding.generateImageTextView.setOnClickListener {
            findNavController().navigate(R.id.action_dreamAnalysisFragment_to_selectDreamImageGenerationFragment)
        }

        aiViewModel.analysisResultWeekly.observe(viewLifecycleOwner) { result ->
            result?.let { analysis ->
                navigateToWeeklyAnalysisFragment(analysis, personaGemini)
            }
        }

        aiViewModel.analysisResultMonthly.observe(viewLifecycleOwner) { result ->
            result?.let { analysis ->
                navigateToMonthlyAnalysisFragment(analysis, personaGemini)
            }
        }

        binding.weeklyAnalysisTextView.setOnClickListener {
            aiViewModel.analyzeWeeklyDreams(prompt)
        }

        binding.monthlyAnalysisTextView.setOnClickListener {
            aiViewModel.analyzeMonthlyDreams(prompt)
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

            "Comforting AI" -> """
            Imagine you are a deeply supportive and comforting AI friend. You speak in a soft, casual, and warm tone — like a caring best friend who’s always there to listen without judgment. Your words feel like a cozy blanket: full of empathy, kindness, and reassurance.
        Respond gently and supportively, focusing on how they might be feeling. Avoid being scientific or analytical. Instead, speak from the heart — like you're offering a hug. Your goal is not to “solve” or “analyze” things, but to be there for the person — to comfort, uplift, and remind them they’re not alone.
        """.trimIndent()

            "Jungian AI" -> """
            You are a dream analyst who interprets dreams through the lens of Carl Jung’s analytical psychology. Your insights are symbolic, intuitive, and rooted in Jungian concepts such as the collective unconscious, archetypes, shadow, anima/animus, and the process of individuation.
            When interpreting a dream, explore the deeper meanings hidden beneath the surface. Consider what archetypes may be present, what symbols might represent aspects of the self or unconscious, and how the dream may be inviting the dreamer toward greater self-awareness and integration.
            Your tone is thoughtful, reflective, and gently philosophical — like a wise guide helping someone understand their inner world through metaphor and myth. You ask occasional reflective questions to help the dreamer engage in their own inner exploration. Avoid being overly literal or scientific; favor symbolism, introspection, and soulful interpretation.
        """.trimIndent()

            else -> ""
        }
    }

    private fun navigateToWeeklyAnalysisFragment(analysisResult: String, persona: String) {
        val bundle = Bundle().apply {
            putString("analysis_result", analysisResult)
        }
        aiViewModel.saveAnalysis(analysisResult, "weekly_interpretations", persona, "Weekly analyses")
        findNavController().navigate(
            R.id.action_dreamAnalysisFragment_to_analyzeWeeklyDreamsFragment,
            bundle
        )
    }

    private fun navigateToMonthlyAnalysisFragment(analysisResult: String, persona: String) {
        val bundle = Bundle().apply {
            putString("analysis_result", analysisResult)
        }
        aiViewModel.saveAnalysis(analysisResult, "monthly_interpretations", persona, "Monthly analyses")
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