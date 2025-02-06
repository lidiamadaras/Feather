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
import com.example.feather.databinding.FragmentMyDreamsBinding
import com.example.feather.models.DreamModel
import com.example.feather.ui.adapter.DreamsAdapter
import com.example.feather.viewmodels.DreamViewModel
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyDreamsFragment : Fragment() {

    private var _binding: FragmentMyDreamsBinding? = null
    private val binding get() = _binding!!

    private lateinit var reflectionEditText: EditText
    private lateinit var saveReflectionButton: Button

    private val dreamViewModel : DreamViewModel by viewModels()

    private lateinit var adapter: DreamsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyDreamsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "My dreams"

        dreamViewModel.getUserDreams()

        adapter = DreamsAdapter(
            listOf(),
            onItemClick = { dream ->
                //navigateToDreamDetail(dream.id)
            },
            onItemLongClick = { dream ->
                //showDeleteConfirmationDialog(dream)  // Show the delete confirmation dialog
            }
        )

        // Set up the RecyclerView
        binding.dreamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.dreamsRecyclerView.adapter = adapter

        binding.dreamsRecyclerView.addItemDecoration(
            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        )


        dreamViewModel.userDreams.observe(viewLifecycleOwner) { dreams ->
            if (!dreams.isNullOrEmpty()) {
                adapter.updateRecipes(dreams)
            } else {
                Toast.makeText(context, "No dreams available", Toast.LENGTH_SHORT).show()
            }
        }

    }

//    private fun navigateToDreamDetail(id: Int) {
//        val bundle = Bundle().apply {
//            putInt("recipeId", id) // Pass only the recipe ID
//        }
//        findNavController().navigate(
//            R.id.action_myDreamsFragment_to_recipeDetailFragment,
//            bundle
//        )
//    }
//
//    private fun showDeleteConfirmationDialog(dream: DreamModel) {
//        val builder = AlertDialog.Builder(requireContext())
//            .setTitle("Delete Dream")
//            .setMessage("Are you sure you want to proceed?")
//            .setPositiveButton("Yes, delete") { dialog, _ ->
//                dreamViewModel.deleteDream(dream)
//                dialog.dismiss()
//            }
//            .setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }
//        builder.create().show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}