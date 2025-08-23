package com.example.running_app.data.repository

import com.example.running_app.data.local.db.RunDAO
import com.example.running_app.data.model.Run
import javax.inject.Inject

class RunRepository @Inject constructor(val runDAO: RunDAO) {

    suspend fun upsertRun(run: Run) = runDAO.upsertRun(run)

    suspend fun deleteRun(run: Run) = runDAO.deleteRun(run)

    fun getFilteredBy(column: String) = runDAO.getFilteredBy(column)

    fun getSUMBy(column: String) = runDAO.getSUMBy(column)

    fun getTotalTimeInMs() = runDAO.getTotalTimeInMs()

    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()
}