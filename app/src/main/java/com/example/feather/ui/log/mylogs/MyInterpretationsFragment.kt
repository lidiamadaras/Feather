package com.example.feather.ui.log.mylogs

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentMyInterpretationsBinding
import com.example.feather.ui.adapter.InterpretationsAdapter
import com.example.feather.viewmodels.ai.AIViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyInterpretationsFragment : Fragment() {

    private var _binding: FragmentMyInterpretationsBinding? = null
    private val binding get() = _binding!!

    private val aiViewModel : AIViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyInterpretationsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My AI Interpretations"

        binding.singleDreamTextView.setOnClickListener{
            findNavController().navigate(R.id.action_myInterpretationsFragment_to_mySingleDreamInterpretationsFragment)
        }

        binding.weeklyTextView.setOnClickListener{
            findNavController().navigate(R.id.action_myInterpretationsFragment_to_myWeeklyInterpretationsFragment)
        }

        binding.monthlyTextView.setOnClickListener{
            findNavController().navigate(R.id.action_myInterpretationsFragment_to_myMonthlyInterpretationsFragment)
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}