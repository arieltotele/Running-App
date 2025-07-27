package com.example.running_app.util

import android.graphics.Color

object Constants {
    const val RUN_DATABASE_NAME = "run_db"

    const val REQUEST_CODE_LOCATION_PERMISSIONS = 0
    const val REQUEST_CODE_NOTIFICATION_PERMISSIONS = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"

    const val LOCATION_UPDATE_DELAY = 1000L
    const val FASTEST_LOCATION_DELAY = 500L
    const val LOCATION_MAX_DELAY_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM_IN_FOCUS = 15f

    const val INTERVAL_TIME_UPDATE = 50L

}