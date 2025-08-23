package com.example.running_app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.running_app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.userProfileDataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.USER_PROFILE_PREFERENCES
)

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserProfileDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return appContext.userProfileDataStore
    }
}