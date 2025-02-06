package com.example.feather.ui.stats

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.databinding.FragmentStatsBinding

class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Stats"

        binding.myDreamsTextView.setOnClickListener {
            findNavController().navigate(R.id.myDreamsFragment)
        }

        binding.myFeelingsTextView.setOnClickListener {
            findNavController().navigate(R.id.myFeelingsFragment)
        }

        binding.myAffirmationsTextView.setOnClickListener {
            findNavController().navigate(R.id.myAffirmationsFragment)
        }

        binding.myReflectionsTextView.setOnClickListener {
            findNavController().navigate(R.id.myReflectionsFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}