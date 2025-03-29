package com.example.feather.ui.explore

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentSymbolsBinding
import com.example.feather.ui.adapter.SymbolsAdapter
import com.example.feather.viewmodels.ExploreViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class SymbolsFragment : Fragment() {

    private var _binding: FragmentSymbolsBinding? = null
    private val binding get() = _binding!!

    private val exploreViewModel : ExploreViewModel by viewModels()

    private lateinit var adapter: SymbolsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSymbolsBinding.inflate(inflater, container, false)

        exploreViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.symbolsRecyclerView.isEnabled = !isLoading
            binding.symbolsRecyclerView.alpha = if (isLoading) 0.5f else 1.0f // Dim when loading
        }


        return binding.root


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exploreViewModel.getSymbols()

        binding.HomeTitleTextView.text = "Symbols"

        //only had to run ONE time, database is filled:
        //exploreViewModel.loadCSVSymbols(requireContext())




        adapter = SymbolsAdapter(
            listOf(),
            onItemClick = { symbol ->
                navigateToSymbolDetail(symbol.id)
            },
            onGeminiClick = {
                //findNavController().navigate(R.id.action_symbolsFragment_to_geminiFragment)

            }
        )

        // Set up the RecyclerView
        binding.symbolsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.symbolsRecyclerView.adapter = adapter

        binding.symbolsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )

        binding.etSearchSymbols.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                exploreViewModel.filterSymbols(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })


        exploreViewModel.symbols.observe(viewLifecycleOwner) { symbols ->
            if (!symbols.isNullOrEmpty()) {
                adapter.updateSymbols(symbols)
            } else {
                Toast.makeText(context, "No symbols available", Toast.LENGTH_SHORT).show()
            }
            //adapter.updateSymbols(symbols) // Always update adapter, even if empty

            if (symbols.isEmpty() && binding.etSearchSymbols.text.isEmpty()) {
                Toast.makeText(context, "No symbols available", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun navigateToSymbolDetail(id: String) {
        val bundle = Bundle().apply {
            putString("symbolId", id) // Pass only the recipe ID
        }
        findNavController().navigate(
            R.id.action_symbolsFragment_to_symbolDetailFragment,
            bundle
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}