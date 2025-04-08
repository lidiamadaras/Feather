package com.example.feather.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentStatisticsBinding
import com.example.feather.models.CalendarDay
import com.example.feather.ui.adapter.CalendarAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Statistics"

        val calendarDays = generateCalendarDays()
        adapter = CalendarAdapter(calendarDays)

        binding.calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        binding.calendarRecyclerView.adapter = adapter


    }

    private fun generateCalendarDays(): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // Make Sunday = 0

        val totalCells = 42 // 6 weeks x 7 days
        val days = mutableListOf<CalendarDay>()

        // Add empty days before the 1st
        repeat(firstDayOfWeek) {
            days.add(CalendarDay(null))
        }

        // Add real days (example logic: random logs)
        for (day in 1..daysInMonth) {
            days.add(
                CalendarDay(
                    dayNumber = day,
                    hasDream = true,
                    hasFeeling = true,
                    hasReflection = true,
                    hasAffirmation = true
                )
            )
        }

        // Fill the rest of the grid with empty cells
        while (days.size < totalCells) {
            days.add(CalendarDay(null))
        }

        return days
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}