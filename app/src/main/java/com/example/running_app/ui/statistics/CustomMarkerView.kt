package com.example.running_app.ui.statistics

import android.content.Context
import android.icu.util.Calendar
import android.view.LayoutInflater
import com.example.running_app.data.model.Run
import com.example.running_app.databinding.MarkerViewBinding
import com.example.running_app.util.TrackingUtility
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
        binding = MarkerViewBinding.inflate(
            LayoutInflater.from(context),
            this, true)
    }

    override fun getOffset(): MPPointF? {
        return MPPointF(-width / 2f, -height.toFloat())

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) return

        val currentRunId = e.x.toInt()
        val run = runs[currentRunId]

        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())

        binding?.apply {
            tvDate.text = dateFormat.format(calendar.time)
            tvDuration.text = TrackingUtility.getFormatStopWatchTime(run.timeInMs)
            tvAvgSpeed.text = "${run.avgSpeedInKMH} km/h"
            tvDistance.text = "${(run.distanceInMts / 1000f)} km"
            tvCaloriesBurned.text = "${run.caloriesBurned} kcal"
        }

        super.refreshContent(e, highlight)
    }

}