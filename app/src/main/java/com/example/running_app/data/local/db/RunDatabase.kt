package com.example.running_app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.running_app.data.local.db.Converters
import com.example.running_app.data.model.Run

@Database(
    entities = [Run::class],
    version = 1,
)
@TypeConverters(Converters::class)
abstract class RunDatabase: RoomDatabase() {

    abstract fun getRunDAO(): RunDAO
}