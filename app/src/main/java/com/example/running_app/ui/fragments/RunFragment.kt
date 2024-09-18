package com.example.running_app.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.running_app.R
import com.example.running_app.ui.viewmodels.RunMainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {
    private val viewModel: RunMainViewModel by viewModels()
}