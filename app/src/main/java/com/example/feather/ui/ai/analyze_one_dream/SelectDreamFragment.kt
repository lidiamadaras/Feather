package com.example.feather.ui.ai.analyze_one_dream

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentSelectDreamBinding
import com.example.feather.ui.adapter.AIDreamsAdapter
import com.example.feather.viewmodels.DreamViewModel
import com.example.feather.viewmodels.ai.AIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectDreamFragment : Fragment() {

    private var _binding: FragmentSelectDreamBinding? = null
    private val binding get() = _binding!!

    private val dreamViewModel : DreamViewModel by viewModels()

    private val aiViewModel : AIViewModel by viewModels()

    private lateinit var adapter: AIDreamsAdapter
    private var personaGemini: String = ""
    private var dreamTitle: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectDreamBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My dreams"

        dreamViewModel.getUserDreams()

        val prompt = arguments?.getString("prompt")

        adapter = AIDreamsAdapter(
            listOf(),
            onItemClick = { dream ->
                if (prompt != null) {
                    dreamTitle = dream.title
                    aiViewModel.analyzeDream(dream, prompt)
                }
            }
        )


        aiViewModel.loadPreferredPersona()

        aiViewModel.preferredPersona.observe(viewLifecycleOwner) { result ->
            result.onSuccess { persona ->
                if (persona != null) {
                    personaGemini = persona
                }
            }

            result.onFailure {
                personaGemini = ""
            }
        }


        // Set up the RecyclerView
        binding.dreamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dreamsRecyclerView.adapter = adapter

        binding.dreamsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )

        aiViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.dreamsRecyclerView.isEnabled = !isLoading
            binding.dreamsRecyclerView.alpha = if (isLoading) 0.5f else 1.0f // Dim when loading
        }


        dreamViewModel.userDreams.observe(viewLifecycleOwner) { dreams ->
            if (!dreams.isNullOrEmpty()) {
                adapter.updateDreams(dreams)
            } else {
                Toast.makeText(context, "No dreams available", Toast.LENGTH_SHORT).show()
            }
        }

        aiViewModel.analysisResult.observe(viewLifecycleOwner) { result ->
            result?.let { analysis ->
                navigateToAnalyzeDreamFragment(analysis, personaGemini, dreamTitle)
            }
        }
    }



    private fun navigateToAnalyzeDreamFragment(analysisResult: String, persona: String, title: String) {
        val bundle = Bundle().apply {
            putString("analysis_result", analysisResult)
        }
        aiViewModel.saveAnalysis(analysisResult, "single_dream_interpretations", persona, title)
        findNavController().navigate(
            R.id.action_selectDreamFragment_to_analyzeDreamFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}