package com.example.feather.ui.ai.analyze_one_dream

import android.os.Bundle
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

        adapter = AIDreamsAdapter(
            listOf(),
            onItemClick = { dream ->
                aiViewModel.analyzeDream(dream)
            }
        )



        // Set up the RecyclerView
        binding.dreamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dreamsRecyclerView.adapter = adapter

        binding.dreamsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        dreamViewModel.userDreams.observe(viewLifecycleOwner) { dreams ->
            if (!dreams.isNullOrEmpty()) {
                adapter.updateDreams(dreams)
            } else {
                Toast.makeText(context, "No dreams available", Toast.LENGTH_SHORT).show()
            }
        }

        aiViewModel.analysisResult.observe(viewLifecycleOwner) { result ->
            result?.let { analysis ->
                navigateToAnalyzeDreamFragment(analysis)
            }
        }
    }



    private fun navigateToAnalyzeDreamFragment(analysisResult: String) {
        val bundle = Bundle().apply {
            putString("analysis_result", analysisResult)
        }
        aiViewModel.saveAnalysis(analysisResult, "single_dream_interpretations")
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