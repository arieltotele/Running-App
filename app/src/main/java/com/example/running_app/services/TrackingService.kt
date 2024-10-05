package com.example.running_app.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.running_app.R
import com.example.running_app.ui.MainActivity
import com.example.running_app.util.Constants.ACTION_PAUSE_SERVICE
import com.example.running_app.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.running_app.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.running_app.util.Constants.ACTION_STOP_SERVICE
import com.example.running_app.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.running_app.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.running_app.util.Constants.NOTIFICATION_ID
import timber.log.Timber

class TrackingService: LifecycleService() {

    private var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun){
                        starForegroundService()
                        isFirstRun  = false
                    }else{
                        Timber.d("Resuming service...")
                    }

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

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE,

    )

    private fun starForegroundService(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            createNotificationChannel(notificationManager)
        }

        val notificationCompatBuilder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_run)
            .setContentTitle("Running App")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationCompatBuilder.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(notificationChannel)
    }
}