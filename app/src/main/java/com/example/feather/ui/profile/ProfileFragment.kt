package com.example.feather.ui.profile

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.activities.AuthActivity
import com.example.feather.databinding.FragmentProfileBinding
import com.example.feather.viewmodels.ai.ApiKeyViewModel
import com.example.feather.viewmodels.auth.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private var isEditing = false   //for changing api key

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.HomeTitleTextView.text = "Profile"

//        authViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }

        binding.profileDataTextView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_profileDataFragment)
        }

        binding.settingsTextView.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}