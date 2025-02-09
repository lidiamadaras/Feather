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
import com.example.feather.models.EmotionModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class EmotionsAdapter(
    var emotions: List<EmotionModel>,
    private val onItemClick: (EmotionModel) -> Unit,          //to details
    private val onItemLongClick: (EmotionModel) -> Unit,      //delete dream

) :
    RecyclerView.Adapter<EmotionsAdapter.EmotionViewHolder>() {

    class EmotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.emotionTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.emotionDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.emotion_recyclerview_row, parent, false)
        return EmotionViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        val emotion = emotions[position]

        // Set text fields
        holder.nameTextView.text = emotion.name
        holder.descriptionTextView.text = emotion.description
        holder.dateTextView.text = "Date:  ${formatTimestamp(emotion.dateAdded)}"

        holder.itemView.setOnClickListener {
            onItemClick(emotion)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(emotion)  // Trigger the onItemLongClick callback when item is long-clicked
            true  // Return true to indicate the long-click event has been handled
        }
    }

    fun updateEmotions(newEmotions: List<EmotionModel>) {
        emotions = newEmotions
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    private fun formatTimestamp(timestamp: Timestamp?): String {
        return if (timestamp != null) {
            val date = timestamp.toDate()
            val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
            sdf.format(date)
        } else {
            "Date: N/A" // if dateAdded is null
        }
    }

    override fun getItemCount(): Int {
        return emotions.size
    }
}