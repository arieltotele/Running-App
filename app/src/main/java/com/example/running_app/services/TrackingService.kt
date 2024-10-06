package com.example.running_app.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.running_app.R
import com.example.running_app.ui.MainActivity
import com.example.running_app.util.Constants.ACTION_PAUSE_SERVICE
import com.example.running_app.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.running_app.util.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.running_app.util.Constants.ACTION_STOP_SERVICE
import com.example.running_app.util.Constants.FASTEST_LOCATION_DELAY
import com.example.running_app.util.Constants.LOCATION_MAX_DELAY_INTERVAL
import com.example.running_app.util.Constants.LOCATION_UPDATE_DELAY
import com.example.running_app.util.Constants.NOTIFICATION_CHANNEL_ID
import com.example.running_app.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.running_app.util.Constants.NOTIFICATION_ID
import com.example.running_app.util.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingService: LifecycleService() {
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var isFirstRun = true

    companion object{
        val isTrackingActive = MutableLiveData<Boolean>()
        val locationPoints = MutableLiveData<Polylines>()
    }

    override fun onCreate() {
        super.onCreate()

        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTrackingActive.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTrackingActive: Boolean){
        if (isTrackingActive){
            if (TrackingUtility.hasLocationPermissions(this)){
                val locationRequest =
                    LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_UPDATE_DELAY)
                        .setWaitForAccurateLocation(true)
                        .setMinUpdateIntervalMillis(FASTEST_LOCATION_DELAY)
                        .setMaxUpdateDelayMillis(LOCATION_MAX_DELAY_INTERVAL)
                        .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    val locationCallback = object : LocationCallback(){
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (isTrackingActive.value!!){
                locationResult?.locations?.let { locations ->
                    for (location in locations){
                        addLocationPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }

                }
            }
        }
    }

    private fun addLocationPoint(location: Location){
        location?.let {
            val position = LatLng(location.latitude, location.longitude)
            locationPoints.value?.apply {
                last().add(position)
                locationPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = locationPoints.value?.apply {
        add(mutableListOf())
        locationPoints.postValue(this)
    } ?: locationPoints.postValue(mutableListOf((mutableListOf())))

    private fun postInitialValues(){
        isTrackingActive.postValue(true)
        locationPoints.postValue(mutableListOf())
    }

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
        addEmptyPolyline()
        isTrackingActive.postValue(true)

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