package com.example.running_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.running_app.repositories.RunRepository
import javax.inject.Inject

class RunStatisticsViewModel @Inject constructor(val runRepository: RunRepository)
    :ViewModel(){

}