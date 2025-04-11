//package com.example.feather.ui.ai.generate_image
//
//import android.app.AlertDialog
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import android.util.Base64
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import com.bumptech.glide.Glide
//import com.example.feather.R
//import com.example.feather.databinding.FragmentAnalyzeDreamBinding
//import com.example.feather.databinding.FragmentGenerateImageBinding
//import com.example.feather.viewmodels.ai.AIViewModel
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class GenerateImageFragment : Fragment() {
//
//    private var _binding: FragmentGenerateImageBinding? = null
//    private val binding get() = _binding!!
//
//    private val aiViewModel : AIViewModel by viewModels()
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentGenerateImageBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val imageUrl = arguments?.getString("image_url")
//
//        // Use Glide to load the image into an ImageView
//        imageUrl?.let {
//            Glide.with(this)
//                .load(it)
//                .into(binding.generatedImageView)
//        } ?: run {
//            // Handle the case where the image URL is not available
//            Toast.makeText(requireContext(), "No image to display", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null // Clear binding to prevent memory leaks
//    }
//}