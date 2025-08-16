package com.example.running_app.repositories

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.running_app.db.UserProfile
import com.example.running_app.util.UserProfilePreferencesKeys.IS_FIRST_APP_OPEN
import com.example.running_app.util.UserProfilePreferencesKeys.USER_NAME
import com.example.running_app.util.UserProfilePreferencesKeys.USER_WEIGHT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
){
    val userProfileFlow: Flow<UserProfile> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val name = preferences[USER_NAME] ?: ""
            val weight = preferences[USER_WEIGHT] ?: 0f
            val isFirstAppOpen = preferences[IS_FIRST_APP_OPEN] ?: true
            UserProfile(name, weight, isFirstAppOpen)
        }

    suspend fun updateUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun updateUserWeight(weight: Float) {
        dataStore.edit { preferences ->
            preferences[USER_WEIGHT] = weight
        }
    }

    suspend fun updateIsFirstAppOpen(isFirstOpen: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_FIRST_APP_OPEN] = isFirstOpen
        }
    }

    suspend fun updateUserProfile(name: String, weight: Float) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
            preferences[USER_WEIGHT] = weight
        }
    }
}