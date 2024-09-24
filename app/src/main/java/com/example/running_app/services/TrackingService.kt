package com.example.running_app.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.example.running_app.util.Constants.ACTION_PAUSE_SERVICE
import com.example.running_app.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.running_app.util.Constants.ACTION_STOP_SERVICE
import timber.log.Timber

class TrackingService: LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    Timber.d("Service started or resumed")
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Service paused")
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Service Stopped")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}