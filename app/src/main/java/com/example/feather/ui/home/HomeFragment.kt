package com.example.feather.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.feather.R
import com.example.feather.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    //database:
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //binding.HomeTitleTextView.text = "Home"

        binding.fabLog.setOnClickListener {
            showPopupMenu(it)
        }

        //welcome user message:
        fetchAndDisplayWelcomeMessage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }

    private fun showPopupMenu(anchor: View) {
        val popupMenu = PopupMenu(requireContext(), anchor)
        popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.action_log_dream -> {
                    findNavController().navigate(R.id.logDreamFragment)
                    true
                }
                R.id.action_log_feeling -> {
                    findNavController().navigate(R.id.logFeelingFragment)
                    true
                }
                R.id.action_homeFragment_to_reflectionFragment -> {
                    findNavController().navigate(R.id.reflectionFragment)
                    true
                }
                R.id.action_homeFragment_to_affirmationFragment -> {
                    findNavController().navigate(R.id.affirmationFragment)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    //trying out database a bit, change later:

    private fun fetchAndDisplayWelcomeMessage() {
        // Access the 'users' collection and get the first document
        firestore.collection("users")
            .limit(1) // Limit to only the first user
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Get the first document and extract the 'firstName' field
                    val firstUser = querySnapshot.documents[0]
                    val firstName = firstUser.getString("firstName") ?: "User"

                    // Update the UI with the welcome message using binding
                    binding.HomeTitleTextView.text = "Welcome, $firstName!"
                } else {
                    // Handle the case where no users are found
                    binding.HomeTitleTextView.text = "Welcome, User!"
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
                //binding.HomeTitleTextView.text = "Error: ${exception.message}"
                binding.HomeTitleTextView.text = "Home"
            }
    }

}