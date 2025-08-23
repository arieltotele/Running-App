package com.example.running_app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.running_app.data.model.Run
import com.example.running_app.data.repository.RunRepository
import com.example.running_app.util.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunStatsViewModel @Inject constructor(val runRepository: RunRepository):ViewModel() {

    private val _sortType = MutableLiveData(SortType.DATE)

    val runs: LiveData<List<Run>> = _sortType.switchMap { sortType ->
        when (sortType) {
            SortType.DATE -> runRepository.getFilteredBy("timestamp")
            SortType.RUNNING_TIME -> runRepository.getFilteredBy("timeInMs")
            SortType.DISTANCE -> runRepository.getFilteredBy("distanceInMts")
            SortType.AVG_SPEED -> runRepository.getFilteredBy("avgSpeedInKMH")
            SortType.CALORIES_BURNED -> runRepository.getFilteredBy("caloriesBurned")
        }
    }

    fun sortRuns(sortType: SortType) {
        _sortType.value = sortType
    }

    fun insertRun(newRun: Run) {
        viewModelScope.launch {
            runRepository.upsertRun(newRun)
        }
    }
}