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
import com.example.feather.databinding.FragmentMyKeywordsBinding
import com.example.feather.models.KeywordModel
import com.example.feather.ui.adapter.KeywordsAdapter
import com.example.feather.viewmodels.DreamViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyKeywordsFragment : Fragment() {

    private var _binding: FragmentMyKeywordsBinding? = null
    private val binding get() = _binding!!

    private val dreamViewModel : DreamViewModel by viewModels()

    private lateinit var adapter: KeywordsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyKeywordsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My keywords"

        dreamViewModel.getUserKeywords()

        adapter = KeywordsAdapter(
            listOf(),
            onItemClick = { keyword ->
                navigateToKeywordDetail(keyword.name)
            },
            onItemLongClick = { keyword ->
                showDeleteConfirmationDialog(keyword)
            }
        )

        // Set up the RecyclerView
        binding.keywordsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.keywordsRecyclerView.adapter = adapter

        binding.keywordsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        dreamViewModel.userKeywords.observe(viewLifecycleOwner) { keywords ->
            if (!keywords.isNullOrEmpty()) {
                adapter.updateKeywords(keywords)
            } else {
                Toast.makeText(context, "No keywords available", Toast.LENGTH_SHORT).show()
            }
        }

        dreamViewModel.deleteKeywordResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Keyword deleted", Toast.LENGTH_SHORT).show()
                dreamViewModel.getUserKeywords()              //immediately refresh list
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error deleting keyword: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigateToKeywordDetail(id: String) {
        val bundle = Bundle().apply {
            putString("keywordId", id) // Pass only the recipe ID
        }
        findNavController().navigate(
            R.id.action_myKeywordsFragment_to_keywordDetailFragment,
            bundle
        )
    }

    private fun showDeleteConfirmationDialog(keyword: KeywordModel) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Delete Keyword")
            .setMessage("Are you sure you want to proceed?")
            .setPositiveButton("Yes, delete") { dialog, _ ->
                dreamViewModel.deleteKeyword(keyword.name)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}