package com.example.running_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.running_app.R
import com.example.running_app.databinding.FragmentStatisticsBinding
import com.example.running_app.ui.viewmodels.RunStatisticsViewModel
import com.example.running_app.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics) {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RunStatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer { totalTime ->
            totalTime?.let {
                val totalTimeRun = TrackingUtility.getFormatStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer { totalDistance ->
            totalDistance?.let {
                val km = totalDistance / 1000f
                val totalDistanceNumber = round(km * 10f) / 10f
                val totalDistanceString = "${totalDistanceNumber}km"
                binding.tvTotalDistance.text = totalDistanceString
            }
            })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer { totalAvgSpeed ->
            totalAvgSpeed.let {
                val avgSpeed = round(totalAvgSpeed * 10f) / 10f
                val avgSpeedString = "${avgSpeed}km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer { totalCaloriesBurned ->
            totalCaloriesBurned?.let {
                val totalCaloriesBurnedString = "${totalCaloriesBurned}kcal"
                binding.tvTotalCalories.text = totalCaloriesBurnedString
            }
        })
    }
}