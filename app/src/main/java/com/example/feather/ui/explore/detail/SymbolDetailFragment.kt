package com.example.feather.ui.explore.detail

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.models.SymbolModel
import com.example.feather.viewmodels.ExploreViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import com.example.feather.databinding.FragmentSymbolDetailBinding

@AndroidEntryPoint
class SymbolDetailFragment : Fragment() {

    private var _binding: FragmentSymbolDetailBinding? = null
    private val binding get() = _binding!!

    private val exploreViewModel : ExploreViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSymbolDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val symbolId = arguments?.getString("symbolId", "") ?: ""

        if (symbolId.isNotBlank()) {
            exploreViewModel.getSymbolById(symbolId)

            exploreViewModel.symbol.observe(viewLifecycleOwner) { selectedSymbol ->
                if (selectedSymbol != null) {
                    displayDreamDetails(selectedSymbol)
                } else {
                    Log.d("Error", "Symbol not found for ID: $symbolId")
                    Toast.makeText(context, "Symbol not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid symbolId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayDreamDetails(selectedSymbol: SymbolModel) {

        binding.HomeTitleTextView.text = selectedSymbol.name

        binding.interpretationTextView.text = selectedSymbol.description
        binding.tagTextView.text = selectedSymbol.tag
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}