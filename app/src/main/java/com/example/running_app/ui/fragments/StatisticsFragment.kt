package com.example.running_app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.running_app.R
import com.example.running_app.databinding.FragmentStatisticsBinding
import com.example.running_app.ui.viewmodels.RunStatisticsViewModel
import com.example.running_app.util.CustomMarkerView
import com.example.running_app.util.TrackingUtility
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
        _binding = FragmentStatisticsBinding.inflate(inflater, container,
            false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setupBarChart()
    }

    private fun setupBarChart(){
        val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)

        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(true) // ← pon esto en true si quieres ver números abajo
            axisLineColor = whiteColor
            textColor = whiteColor
            setDrawGridLines(false)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor = whiteColor
            textColor = whiteColor
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = whiteColor
            textColor = whiteColor
            setDrawGridLines(false)
        }

        binding.barChart.apply {
            description.text = "Avg Speed Over Time"
            description.textColor = whiteColor
            legend.isEnabled = false
        }
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

        viewModel.runsSortedByDate.observe(viewLifecycleOwner){ runsSortedByDate ->
            runsSortedByDate.let {
                val allAvgSpeeds = it.indices.map { i ->
                    BarEntry(
                        i.toFloat(),
                        it[i].avgSpeedInKMH
                    )
                }
                val barDataSet = BarDataSet(allAvgSpeeds,
                    "Avg Speed Over Time").apply {
                    valueTextColor = R.color.white
                    color = ContextCompat.getColor(requireContext(),
                        R.color.colorAccent)
                }

                binding.barChart.data = BarData(barDataSet)
                binding.barChart.marker =
                    CustomMarkerView(it.reversed(), requireContext(),
                        R.layout.marker_view)
                binding.barChart.invalidate()

            }
        }
    }
}