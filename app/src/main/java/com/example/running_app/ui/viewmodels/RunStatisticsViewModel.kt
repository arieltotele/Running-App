package com.example.running_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.running_app.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RunStatisticsViewModel @Inject constructor(val runRepository: RunRepository)
    :ViewModel(){

        val totalTimeRun = runRepository.getTotalTimeInMs()
        val totalDistance = runRepository.getSUMBy("distanceInMts")
        val totalCaloriesBurned = runRepository.getSUMBy("caloriesBurned")
        val totalAvgSpeed = runRepository.getTotalAvgSpeed()

        val runsSortedByDate = runRepository.getFilteredBy("timestamp")
}