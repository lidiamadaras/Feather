package com.example.feather.ui.log.mylogs.details

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
import com.example.feather.databinding.FragmentKeywordDetailBinding
import com.example.feather.models.KeywordModel
import com.example.feather.viewmodels.details.KeywordDetailViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class KeywordDetailFragment : Fragment() {

    private var _binding: FragmentKeywordDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var keywordTextView: TextView

    private val keywordDetailViewModel : KeywordDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKeywordDetailBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keywordId = arguments?.getString("keywordId", "") ?: ""

        if (keywordId.isNotBlank()) {
            keywordDetailViewModel.getKeywordById(keywordId)

            keywordDetailViewModel.keyword.observe(viewLifecycleOwner) { selectedKeyword ->
                if (selectedKeyword != null) {
                    displayKeywordDetails(selectedKeyword)
                } else {
                    Log.d("Error", "Keyword not found for ID: $keywordId")
                    Toast.makeText(context, "Keyword not found", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Invalid keywordId", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayKeywordDetails(selectedKeyword: KeywordModel) {
        keywordTextView = binding.keywordTextView

        binding.HomeTitleTextView.text = "Keyword"

        keywordTextView.text = selectedKeyword.name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}