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
import com.example.feather.models.ReflectionModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ReflectionsAdapter(
    var reflections: List<ReflectionModel>,
    private val onItemClick: (ReflectionModel) -> Unit,          //to details
    private val onItemLongClick: (ReflectionModel) -> Unit,      //delete dream

) :
    RecyclerView.Adapter<ReflectionsAdapter.ReflectionViewHolder>() {

    class ReflectionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.reflectionTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.reflectionDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReflectionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reflection_recyclerview_row, parent, false)
        return ReflectionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReflectionViewHolder, position: Int) {
        val reflection = reflections[position]

        // Set text fields
        holder.nameTextView.text = reflection.text
        holder.dateTextView.text = "Date:  ${formatTimestamp(reflection.dateAdded)}"

        holder.itemView.setOnClickListener {
            onItemClick(reflection)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(reflection)  // Trigger the onItemLongClick callback when item is long-clicked
            true  // Return true to indicate the long-click event has been handled
        }
    }

    fun updateReflections(newReflections: List<ReflectionModel>) {
        reflections = newReflections
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
        return reflections.size
    }
}