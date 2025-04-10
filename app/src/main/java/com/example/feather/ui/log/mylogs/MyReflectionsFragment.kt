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
import com.example.feather.databinding.FragmentMyReflectionsBinding
import com.example.feather.models.ReflectionModel
import com.example.feather.ui.adapter.ReflectionsAdapter
import com.example.feather.viewmodels.ReflectionViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyReflectionsFragment : Fragment() {

    private var _binding: FragmentMyReflectionsBinding? = null
    private val binding get() = _binding!!

    private val reflectionViewModel : ReflectionViewModel by viewModels()

    private lateinit var adapter: ReflectionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyReflectionsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My reflections"

        reflectionViewModel.getUserReflections()

        adapter = ReflectionsAdapter(
            listOf(),
            onItemClick = { reflection ->
                navigateToReflectionDetail(reflection.id)
            },
            onItemLongClick = { reflection ->
                showDeleteConfirmationDialog(reflection)
            }
        )

        // Set up the RecyclerView
        binding.reflectionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.reflectionsRecyclerView.adapter = adapter

        binding.reflectionsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        reflectionViewModel.userReflections.observe(viewLifecycleOwner) { reflections ->
            if (!reflections.isNullOrEmpty()) {
                adapter.updateReflections(reflections)
            } else {
                Toast.makeText(context, "No reflections available", Toast.LENGTH_SHORT).show()
            }
        }

        reflectionViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Reflection deleted", Toast.LENGTH_SHORT).show()
                reflectionViewModel.getUserReflections()              //immediately refresh list
            }
            result.onFailure { exception ->
                Toast.makeText(requireContext(), "Error deleting reflection: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun navigateToReflectionDetail(id: String) {
        val bundle = Bundle().apply {
            putString("reflectionId", id) // Pass only the recipe ID
        }
        findNavController().navigate(
            R.id.action_myReflectionsFragment_to_reflectionDetailFragment,
            bundle
        )
    }

    private fun showDeleteConfirmationDialog(reflection: ReflectionModel) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle("Delete Reflection")
            .setMessage("Are you sure you want to proceed?")
            .setPositiveButton("Yes, delete") { dialog, _ ->
                reflectionViewModel.deleteReflection(reflection.id)
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


