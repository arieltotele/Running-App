package com.example.running_app.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import android.view.Menu
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
import com.example.running_app.util.Constants.INTERVAL_TIME_UPDATE
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService: LifecycleService() {

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var isFirstRun = true

    private val timeRunInSeconds = MutableLiveData<Long>()

    private var isTimerEnabled = true
    private var lapStartTime = 0L
    private var totalTimeOfRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var currentNotificationBuilder: NotificationCompat.Builder

   private var isServiceKilled = false

    companion object{
        val isTrackingActive = MutableLiveData<Boolean>()
        val locationPoints = MutableLiveData<Polylines>()
        val timeRunInMilliseconds = MutableLiveData<Long>()
    }

    override fun onCreate() {
        super.onCreate()
        currentNotificationBuilder = baseNotificationBuilder
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        isTrackingActive.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })
    }

    private fun killService(){
        isServiceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValues()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(Service.STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }

    private fun startTimer(){
        addEmptyPolyline()
        isTrackingActive.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while (isTrackingActive.value!!){
                // the time passed between now and the time when it started (timeStarted)
                lapStartTime = System.currentTimeMillis() - timeStarted
                //A new time adding the new lap time
                timeRunInMilliseconds.postValue(totalTimeOfRun + lapStartTime)
                if(timeRunInMilliseconds.value!! >= lastSecondTimestamp + 100L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(INTERVAL_TIME_UPDATE)
            }
            totalTimeOfRun += lapStartTime
        }
    }

    private fun updateNotificationTrackingState(isTrackingActive: Boolean){
        val notificationActionText = if (isTrackingActive) "Pause" else "Resume"
        val pendingIntent = if (isTrackingActive){
            val pauseIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this, 1, pauseIntent,
                FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        }else {
            val resumeIntent = Intent(this, TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this, 2, resumeIntent,
                FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        currentNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(currentNotificationBuilder, ArrayList<NotificationCompat.Action>())
        }

        if (!isServiceKilled){
            currentNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.ic_pause_black, notificationActionText, pendingIntent)
            notificationManager.notify(NOTIFICATION_ID, currentNotificationBuilder.build())
        }
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
                locationResult.locations.let { locations ->
                    for (location in locations){
                        addLocationPoint(location)
                        Timber.d("NEW LOCATION: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addLocationPoint(location: Location){
        location.let {
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
        timeRunInSeconds.postValue(0L)
        timeRunInMilliseconds.postValue(0L)
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
                        startTimer()
                    }
                }
                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Service paused")
                    pauseService()
                }
                ACTION_STOP_SERVICE -> {
                    Timber.d("Service Stopped")
                    killService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService(){
        isTrackingActive.postValue(false)
        isTimerEnabled = false
    }

    private fun starForegroundService(){
        startTimer()
        isTrackingActive.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID, baseNotificationBuilder.build())

        timeRunInSeconds.observe(this, Observer{
            if (!isServiceKilled){
                val notification = currentNotificationBuilder
                    .setContentText(TrackingUtility.getFormatStopWatchTime(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID, notification.build())
            }
        })
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW)

        notificationManager.createNotificationChannel(notificationChannel)
    }
}