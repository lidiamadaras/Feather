package com.example.feather.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.feather.R
import com.example.feather.models.FeelingModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.sql.DataSource

class FeelingsAdapter(
    var feelings: List<FeelingModel>,
    private val onItemClick: (FeelingModel) -> Unit,          //to details
    private val onItemLongClick: (FeelingModel) -> Unit,      //delete dream

) :
    RecyclerView.Adapter<FeelingsAdapter.FeelingViewHolder>() {

    class FeelingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.feelingTitleTextView)
        val intensityTextView: TextView = itemView.findViewById(R.id.feelingIntensityTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.feelingDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeelingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feeling_recyclerview_row, parent, false)
        return FeelingViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeelingViewHolder, position: Int) {
        val feeling = feelings[position]

        holder.nameTextView.text = feeling.emotion
        holder.intensityTextView.text = "Intensity: ${feeling.intensity}"
        holder.dateTextView.text = "Date:  ${formatTimestamp(feeling.dateAdded)}"

        holder.itemView.setOnClickListener {
            onItemClick(feeling)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(feeling)  // Trigger the onItemLongClick callback when item is long-clicked
            true  // Return true to indicate the long-click event has been handled
        }
    }

    fun updateFeelings(newFeelings: List<FeelingModel>) {
        feelings = newFeelings
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    private fun formatTimestamp(timestamp: Timestamp?): String {
        return if (timestamp != null) {
            val date = timestamp.toDate()
            val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            sdf.format(date)
        } else {
            "Date: N/A"
        }
    }

    override fun getItemCount(): Int {
        return feelings.size
    }
}