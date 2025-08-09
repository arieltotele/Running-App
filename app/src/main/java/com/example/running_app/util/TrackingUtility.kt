package com.example.running_app.util

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.running_app.services.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun hasNotificationPermission(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        )

    fun getFormatStopWatchTime(millis: Long, isIncludingMillis: Boolean = false): String{
        var milliseconds = millis
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliseconds /= 10

        return "${if(hours < 10) "0" else ""}$hours:" +
                "${if(minutes < 10) "0" else ""}$minutes:" +
                "${if(seconds < 10) "0" else ""}$seconds:" +
                if(isIncludingMillis) "${if(milliseconds < 10) "0" else ""}$milliseconds" else ""
    }

    fun calculatePolylineDistance(polyline: Polyline): Float {
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val firstPosition = polyline[i]
            val secondPosition = polyline[i + 1]
            val result = FloatArray(1)
            Location.distanceBetween(
                firstPosition.latitude,
                firstPosition.longitude,
                secondPosition.latitude,
                secondPosition.longitude,
                result
            )
            distance += result[0]
        }

        return distance
    }
}