package com.example.feather.ui.ai.generate_image

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.feather.R
import com.example.feather.databinding.FragmentAnalyzeDreamBinding
import com.example.feather.databinding.FragmentGenerateImageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenerateImageFragment : Fragment() {

    private var _binding: FragmentGenerateImageBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGenerateImageBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val base64Image = arguments?.getString("generated_image")
        if (base64Image != null) {
            val bitmap = decodeBase64ToBitmap(base64Image)
            binding.generatedImageView.setImageBitmap(bitmap) // Display image
        }
    }

    private fun decodeBase64ToBitmap(base64Image: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear binding to prevent memory leaks
    }
}