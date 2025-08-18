package com.example.running_app.util

import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import com.example.running_app.databinding.MarkerViewBinding
import com.example.running_app.db.Run
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Locale

class CustomMarkerView (val runs: List<Run>, context: Context, layoutId: Int) :
    MarkerView(context, layoutId){

        private var binding: MarkerViewBinding? = null

    init {
        binding = MarkerViewBinding.inflate(LayoutInflater.from(context),
            this, true)
    }

    override fun getOffset(): MPPointF? {
        return MPPointF(-width / 2f, -height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) {
            return
        }
        val currentRunId = e.x.toInt()
        val run = runs[currentRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        binding!!.tvDate.text = dateFormat.format(calendar.time)

        val avgSpeed = "${run.avgSpeedInKMH}km/h"
        binding!!.tvAvgSpeed.text = avgSpeed

        val distanceInKm = "${run.distanceInMts / 1000f}km"
        binding!!.tvDistance.text = distanceInKm

        binding!!.tvDuration.text = TrackingUtility.getFormatStopWatchTime(run.timeInMs)

        val caloriesBurned = "${run.caloriesBurned}kcal"
        binding!!.tvCaloriesBurned.text = caloriesBurned

        super.refreshContent(e, highlight)
    }
}