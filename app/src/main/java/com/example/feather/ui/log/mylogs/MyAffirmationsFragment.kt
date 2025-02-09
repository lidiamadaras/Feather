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
import com.example.feather.databinding.FragmentMyAffirmationsBinding
import com.example.feather.models.AffirmationModel
import com.example.feather.ui.adapter.AffirmationsAdapter
import com.example.feather.viewmodels.AffirmationViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyAffirmationsFragment : Fragment() {

    private var _binding: FragmentMyAffirmationsBinding? = null
    private val binding get() = _binding!!

    private val affirmationViewModel : AffirmationViewModel by viewModels()

    private lateinit var adapter: AffirmationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAffirmationsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My feelings"

        affirmationViewModel.getUserAffirmations()

        adapter = AffirmationsAdapter(
            listOf(),
            onItemClick = { affirmation ->
                navigateToAffirmationDetail(affirmation.id)
            },
            onItemLongClick = { affirmation ->
                showDeleteConfirmationDialog(affirmation)
            }
        )

        // Set up the RecyclerView
        binding.affirmationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.affirmationsRecyclerView.adapter = adapter

        binding.affirmationsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        affirmationViewModel.userAffirmations.observe(viewLifecycleOwner) { affirmations ->
            if (!affirmations.isNullOrEmpty()) {
                adapter.updateAffirmations(affirmations)
            } else {
                Toast.makeText(context, "No affirmations available", Toast.LENGTH_SHORT).show()
            }
        }

        affirmationViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Affirmation deleted", Toast.LENGTH_SHORT).show()
                affirmationViewModel.getUserAffirmations()              //immediately refresh list
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error deleting affirmation: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigateToAffirmationDetail(id: String) {
        val bundle = Bundle().apply {
            putString("affirmationId", id) // Pass only the recipe ID
        }
        findNavController().navigate(
            R.id.action_myAffirmationsFragment_to_affirmationDetailFragment,
            bundle
        )
    }

    private fun showDeleteConfirmationDialog(affirmation: AffirmationModel) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Delete Affirmation")
            .setMessage("Are you sure you want to proceed?")
            .setPositiveButton("Yes, delete") { dialog, _ ->
                affirmationViewModel.deleteAffirmation(affirmation.id)
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