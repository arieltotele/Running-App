package com.example.running_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.running_app.db.Run
import com.example.running_app.repositories.RunRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunMainViewModel @Inject constructor(val runRepository: RunRepository):ViewModel() {

    val runsSortedByDate = runRepository.getFilteredBy("timestamp")

    fun insertRun(newRun: Run) {
        viewModelScope.launch {
            runRepository.upsertRun(newRun)
        }
    }
}