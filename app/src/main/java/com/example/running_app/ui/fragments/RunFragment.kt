package com.example.running_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.running_app.R
import com.example.running_app.databinding.FragmentRunBinding
import com.example.running_app.ui.MainActivity
import com.example.running_app.ui.viewmodels.RunMainViewModel
import com.example.running_app.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {
    private val viewModel: RunMainViewModel by viewModels()
    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRunBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).requestPermissions(true)

        binding.btnAdd.setOnClickListener {
            if(TrackingUtility.hasLocationPermissions(requireContext())){
                val action = RunFragmentDirections.toTrackingFragment()
                findNavController().navigate(action)
            }else{
                (activity as MainActivity).requestNotificationPermissions()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}