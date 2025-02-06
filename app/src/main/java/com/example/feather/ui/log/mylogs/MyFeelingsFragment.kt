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
import com.example.feather.databinding.FragmentMyFeelingsBinding
import com.example.feather.models.FeelingModel
import com.example.feather.ui.adapter.FeelingsAdapter
import com.example.feather.viewmodels.FeelingViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyFeelingsFragment : Fragment() {

    private var _binding: FragmentMyFeelingsBinding? = null
    private val binding get() = _binding!!

    private val feelingViewModel : FeelingViewModel by viewModels()

    private lateinit var adapter: FeelingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyFeelingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My feelings"

        feelingViewModel.getUserFeelings()

        adapter = FeelingsAdapter(
            listOf(),
            onItemClick = { feeling ->
                navigateToDreamDetail(feeling.id)
            },
            onItemLongClick = { feeling ->
                showDeleteConfirmationDialog(feeling)
            }
        )

        // Set up the RecyclerView
        binding.feelingsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.feelingsRecyclerView.adapter = adapter

        binding.feelingsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        feelingViewModel.userFeelings.observe(viewLifecycleOwner) { feelings ->
            if (!feelings.isNullOrEmpty()) {
                adapter.updateFeelings(feelings)
            } else {
                Toast.makeText(context, "No feelings available", Toast.LENGTH_SHORT).show()
            }
        }

        feelingViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Dream deleted", Toast.LENGTH_SHORT).show()
                feelingViewModel.getUserFeelings()              //immediately refresh list
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error deleting feeling: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigateToFeelingDetail(id: String) {
        val bundle = Bundle().apply {
            putString("feelingId", id) // Pass only the recipe ID
        }
        findNavController().navigate(
            R.id.action_myDreamsFragment_to_feelingDetailFragment,
            bundle
        )
    }

    private fun showDeleteConfirmationDialog(feeling: FeelingModel) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Delete Feeling")
            .setMessage("Are you sure you want to proceed?")
            .setPositiveButton("Yes, delete") { dialog, _ ->
                feelingViewModel.deleteFeeling(feeling.id)
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