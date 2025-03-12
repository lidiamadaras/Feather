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
import com.example.feather.models.DreamModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.sql.DataSource

class AIDreamsAdapter(
    var dreams: List<DreamModel>,
    private val onItemClick: (DreamModel) -> Unit,          //to details

) :
    RecyclerView.Adapter<AIDreamsAdapter.DreamViewHolder>() {

    class DreamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.dreamTitleTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.dreamCategoryTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dreamDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DreamViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dream_recyclerview_row, parent, false)
        return DreamViewHolder(view)
    }

    override fun onBindViewHolder(holder: DreamViewHolder, position: Int) {
        val dream = dreams[position]

        // Set text fields
        holder.nameTextView.text = dream.title
        holder.categoryTextView.text = "Category: ${dream.category}"
        holder.dateTextView.text = "Date:  ${formatTimestamp(dream.dateAdded)}"

        holder.itemView.setOnClickListener {
            onItemClick(dream)
        }
    }

    fun updateDreams(newDreams: List<DreamModel>) {
        dreams = newDreams
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
        return dreams.size
    }
}