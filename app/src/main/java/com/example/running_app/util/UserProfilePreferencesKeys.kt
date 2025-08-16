package com.example.running_app.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserProfilePreferencesKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_WEIGHT = floatPreferencesKey("user_weight")
    val IS_FIRST_APP_OPEN = booleanPreferencesKey("is_first_app_open")
}