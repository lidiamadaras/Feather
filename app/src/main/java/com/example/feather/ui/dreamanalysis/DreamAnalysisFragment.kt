package com.example.feather.ui.dreamanalysis

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.databinding.FragmentDreamanalysisBinding

class DreamAnalysisFragment : Fragment() {

    private var _binding: FragmentDreamanalysisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDreamanalysisBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Dream analysis"

        binding.analyzeOneDreamTextView.setOnClickListener {
            findNavController().navigate(R.id.action_dreamAnalysisFragment_to_selectDreamFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}