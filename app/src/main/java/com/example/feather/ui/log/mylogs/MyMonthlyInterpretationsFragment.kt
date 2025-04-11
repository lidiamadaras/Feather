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
import com.example.feather.databinding.FragmentMyMonthlyInterpretationsBinding
import com.example.feather.models.DreamInterpretationModel
import com.example.feather.ui.adapter.InterpretationsAdapter
import com.example.feather.viewmodels.ai.AIViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyMonthlyInterpretationsFragment : Fragment() {

    private var _binding: FragmentMyMonthlyInterpretationsBinding? = null
    private val binding get() = _binding!!

    private val aiViewModel : AIViewModel by viewModels()

    private lateinit var adapter: InterpretationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyMonthlyInterpretationsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My monthly dream interpretations"

        aiViewModel.getUserInterpretations("monthly_interpretations")

        adapter = InterpretationsAdapter(
            listOf(),
            onItemClick = { interpretation ->
                navigateToInterpretationDetail(interpretation.id, "monthly_interpretations" )
            },
            onItemLongClick = { interpretation ->
                showDeleteConfirmationDialog(interpretation, "monthly_interpretations")
            }
        )

        // Set up the RecyclerView
        binding.interpretationsMonthlyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.interpretationsMonthlyRecyclerView.adapter = adapter

        binding.interpretationsMonthlyRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        aiViewModel.userInterpretations.observe(viewLifecycleOwner) { interpretations ->
            if (!interpretations.isNullOrEmpty()) {
                adapter.updateInterpretations(interpretations)
            } else {
                Toast.makeText(context, "No interpretations available", Toast.LENGTH_SHORT).show()
            }
        }

        aiViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Interpretation deleted", Toast.LENGTH_SHORT).show()
                aiViewModel.getUserInterpretations("monthly_interpretations")              //immediately refresh list
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error deleting interpretation: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigateToInterpretationDetail(id: String, type: String) {
        val bundle = Bundle().apply {
            putString("id", id)
            putString("type", type)
        }
        findNavController().navigate(
            R.id.action_myMonthlyInterpretationsFragment_to_interpretationDetailFragment,
            bundle
        )
    }

    private fun showDeleteConfirmationDialog(interpretation: DreamInterpretationModel, type: String) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Delete interpretation")
            .setMessage("Are you sure you want to proceed?")
            .setPositiveButton("Yes, delete") { dialog, _ ->
                aiViewModel.deleteInterpretation(interpretation.id, type)
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

