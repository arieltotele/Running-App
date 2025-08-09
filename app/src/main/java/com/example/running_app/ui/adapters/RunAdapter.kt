package com.example.running_app.ui.adapters

import android.icu.util.Calendar
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.running_app.databinding.ItemRunBinding
import com.example.running_app.db.Run
import com.example.running_app.util.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Locale

class RunAdapter (private val onItemClicked: (Run) -> Unit)
    : ListAdapter<Run, RunAdapter.RunViewHolder>(DiffCallback()) {
    inner class RunViewHolder(private val binding: ItemRunBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val run = getItem(position)
                    onItemClicked(run)
                }
            }
        }

        fun bind(run: Run) {
            Glide.with(this.itemView).load(run.img).into(binding.ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(calendar.time)

            val avgSpeed = "${run.avgSpeedInKMH}km/h"
            binding.tvAvgSpeed.text = avgSpeed

            val distanceInKm = "${run.distanceInMts / 1000f}km"
            binding.tvDistance.text = distanceInKm

            binding.tvTime.text = TrackingUtility.getFormatStopWatchTime(run.timeInMs)

            val caloriesBurned = "${run.caloriesBurned}kcal"
            binding.tvCalories.text = caloriesBurned

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding = ItemRunBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RunViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RunViewHolder,
        position: Int
    ) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class DiffCallback: DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
}

