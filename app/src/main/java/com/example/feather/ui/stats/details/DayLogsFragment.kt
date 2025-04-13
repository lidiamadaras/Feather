package com.example.feather.ui.stats.details

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentDayLogsBinding
import com.example.feather.models.CalendarDay
import com.example.feather.ui.adapter.CalendarAdapter
import com.example.feather.viewmodels.StatisticsViewModel
import com.example.feather.viewmodels.ai.ApiKeyViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class DayLogsFragment : Fragment() {

    private var _binding: FragmentDayLogsBinding? = null
    private val binding get() = _binding!!

    private val statsViewModel: StatisticsViewModel by viewModels()


    private var day: Int = 0
    private var month: Int = 1
    private var year: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            day = it.getInt("day")
            month = it.getInt("month")
            year = it.getInt("year")
        }

        Log.d("Calendar", "$year.$month.$day")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDayLogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statsViewModel.fetchReflectionsForDay(day, month, year)
        statsViewModel.fetchAffirmationsForDay(day, month, year)
        statsViewModel.fetchFeelingsForDay(day, month, year)
        statsViewModel.fetchDreamsForDay(day, month, year)

        observeLogCounts()
    }

    private fun observeLogCounts() {
        val containerLayout = binding.logsListContainer

        statsViewModel.reflectionsForDay.observe(viewLifecycleOwner) { reflections ->
            addLogItem(containerLayout, "Reflections", reflections.size)
            Log.d("Calendar", "Reflections: $reflections" )
        }

        statsViewModel.affirmationsForDay.observe(viewLifecycleOwner) { affirmations ->
            addLogItem(containerLayout, "Affirmations", affirmations.size)
            Log.d("Calendar", "Affirmations: $affirmations" )
        }

        statsViewModel.feelingsForDay.observe(viewLifecycleOwner) { feelings ->
            addLogItem(containerLayout, "Feelings", feelings.size)
            Log.d("Calendar", "Feelings: $feelings" )
        }

        statsViewModel.dreamsForDay.observe(viewLifecycleOwner) { dreams ->
            addLogItem(containerLayout, "Dreams", dreams.size)
            Log.d("Calendar", "Dreams: $dreams" )
        }
    }


    private fun addLogItem(container: LinearLayout, type: String, count: Int) {
        val itemView = TextView(requireContext())
        itemView.text = "$type ($count)"
        itemView.setPadding(20, 20, 20, 20)
        container.addView(itemView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}