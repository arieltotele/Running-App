package com.example.running_app.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RunDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("""
        SELECT * FROM running_table
        ORDER BY 
        CASE WHEN :column = 'timestamp'  THEN timestamp END DESC,
        CASE WHEN :column = 'timeinms' THEN timeInMs END DESC,
        CASE WHEN :column = 'calories' THEN caloriesBurned END DESC,
        CASE WHEN :column = 'speed'  THEN avgSpeedInKMH END DESC,
        CASE WHEN :column = 'distance' THEN distanceInMts END DESC
    """)
    fun filterBy(column : String) : LiveData<List<Run>>

    @Query("SELECT SUM(timeInMs) FROM running_table")
    fun getTotalTimeInMs(): LiveData<Long>

    @Query("""
        SELECT 
            CASE WHEN :column = 'calories' THEN SUM(caloriesBurned)
                 WHEN :column = 'distance' THEN SUM(distanceInMts)
                 ELSE 0 END as result
        FROM running_table
    """)
    fun getSUMBy(column: String): LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed(): LiveData<Float>
}