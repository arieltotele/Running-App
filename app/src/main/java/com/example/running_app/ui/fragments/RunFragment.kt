package com.example.running_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.running_app.R
import com.example.running_app.databinding.FragmentRunBinding
import com.example.running_app.db.Run
import com.example.running_app.ui.MainActivity
import com.example.running_app.ui.adapters.RunAdapter
import com.example.running_app.ui.viewmodels.RunMainViewModel
import com.example.running_app.util.SortType
import com.example.running_app.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {
    private val viewModel: RunMainViewModel by viewModels()
    private var _binding: FragmentRunBinding? = null
    private val binding get() = _binding!!
    private lateinit var runAdapter: RunAdapter

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

        setupRecyclerView()

        viewModel.runs.observe(viewLifecycleOwner, Observer { runList ->
            runAdapter.submitList(runList)
        })

        binding.spnFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position){
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.btnAdd.setOnClickListener {
            if(TrackingUtility.hasLocationPermissions(requireContext())){
                val action = RunFragmentDirections.toTrackingFragment()
                findNavController().navigate(action)
            }else{
                (activity as MainActivity).requestNotificationPermissions()
            }
        }
    }

    private fun setupRecyclerView(){
        runAdapter = RunAdapter{ clickedItemRun: Run ->
            Toast.makeText(requireContext(), "Clicked item: ${clickedItemRun.id}",
                Toast.LENGTH_SHORT).show()
        }

        binding.rvRuns.apply {
            adapter = runAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}