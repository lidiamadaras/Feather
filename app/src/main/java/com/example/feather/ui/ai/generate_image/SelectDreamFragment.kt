//package com.example.feather.ui.ai.generate_image
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.DividerItemDecoration
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.feather.R
//import com.example.feather.databinding.FragmentSelectDreamBinding
//import com.example.feather.ui.adapter.AIDreamsAdapter
//import com.example.feather.viewmodels.DreamViewModel
//import com.example.feather.viewmodels.ai.AIViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import java.io.ByteArrayOutputStream
//import android.util.Base64
//import android.util.Log
//import com.example.feather.models.DreamModel
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.File
//import java.io.FileOutputStream
//
//@AndroidEntryPoint
//class SelectDreamFragment : Fragment() {
//
//    private var _binding: FragmentSelectDreamBinding? = null
//    private val binding get() = _binding!!
//
//    private val dreamViewModel : DreamViewModel by viewModels()
//
//    private val aiViewModel : AIViewModel by viewModels()
//
//    private lateinit var adapter: AIDreamsAdapter
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentSelectDreamBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        binding.HomeTitleTextView.text = "My dreams"
//
//        dreamViewModel.getUserDreams()
//
//
//
//
//
//        // Set up the RecyclerView
//        binding.dreamsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        binding.dreamsRecyclerView.adapter = adapter
//
//        binding.dreamsRecyclerView.addItemDecoration(
//            DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
//        )
//
//        aiViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//            binding.dreamsRecyclerView.isEnabled = !isLoading
//            binding.dreamsRecyclerView.alpha = if (isLoading) 0.5f else 1.0f // Dim when loading
//        }
//
//
//        dreamViewModel.userDreams.observe(viewLifecycleOwner) { dreams ->
//            if (!dreams.isNullOrEmpty()) {
//                adapter.updateDreams(dreams)
//            } else {
//                Toast.makeText(context, "No dreams available", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null // Clear binding to prevent memory leaks
//    }
//}