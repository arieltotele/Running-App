package com.example.running_app.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.running_app.ui.viewmodels.RunStatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val viewModel: RunStatisticsViewModel by viewModels()
}