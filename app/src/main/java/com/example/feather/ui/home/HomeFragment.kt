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
import com.example.feather.viewmodels.AffirmationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val affirmationViewModel : AffirmationViewModel by viewModels()

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

        Log.d("HomeFragment", "onViewCreated called. Binding initialized? ${_binding != null}")

        //binding.HomeTitleTextView.text = "Home"

        binding.fabLog.setOnClickListener {
            showPopupMenu(it)
        }

        val affirmationTextView = binding.affirmationTextView

        affirmationViewModel.randomUserAffirmation.observe(viewLifecycleOwner) { affirmation ->
            if (affirmation != null) {
                affirmationTextView.text = affirmation.text
            }else {
                affirmationTextView.setOnClickListener(){
                    findNavController().navigate(R.id.affirmationFragment)
                }
            }
        }

        affirmationViewModel.getRandomUserAffirmation()

        //welcome user message:
        fetchAndDisplayWelcomeMessage()
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
        val auth = FirebaseAuth.getInstance() // Initialize FirebaseAuth
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Log.e("HomeFragment", "User is not logged in!")
            _binding?.HomeTitleTextView?.text = "Welcome, User!"
            return
        }

        Log.d("HomeFragment", "Fetching user data for UID: ${currentUser.uid}")

        firestore.collection("users")
            .whereEqualTo("uid", currentUser.uid) // Search by the stored 'uid' field
            .limit(1) // Should only return one document
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val firstName = document.getString("firstName") ?: "User"
                    Log.d("HomeFragment", "User data found: First Name = $firstName")
                    _binding?.HomeTitleTextView?.text = "Welcome, $firstName!"
                } else {
                    Log.e("HomeFragment", "User document does not exist in Firestore!")
                    _binding?.HomeTitleTextView?.text = "Welcome, User!"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error fetching user data: ${exception.message}")
                _binding?.HomeTitleTextView?.text = "Home"
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}