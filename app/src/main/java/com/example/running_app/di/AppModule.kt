package com.example.running_app.di

import android.content.Context
import androidx.room.Room
import com.example.running_app.data.local.db.RunDatabase
import com.example.running_app.util.Constants.RUN_DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            RunDatabase::class.java,
            RUN_DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun provideRunDAO(db: RunDatabase) = db.getRunDAO()
}