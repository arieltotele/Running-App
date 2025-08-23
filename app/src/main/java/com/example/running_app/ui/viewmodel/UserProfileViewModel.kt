package com.example.running_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.running_app.data.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
): ViewModel() {

    val userProfile = userProfileRepository.userProfileFlow.asLiveData()

    fun setAppOpened() {
        viewModelScope.launch {
            userProfileRepository.updateIsFirstAppOpen(false)
        }
    }
    fun saveUserProfile(name: String, weight: Float) {
        viewModelScope.launch {
            userProfileRepository.updateUserProfile(name, weight)
        }
    }
}