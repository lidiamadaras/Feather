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
import com.example.feather.models.AffirmationModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class AffirmationsAdapter(
    var affirmations: List<AffirmationModel>,
    private val onItemClick: (AffirmationModel) -> Unit,          //to details
    private val onItemLongClick: (AffirmationModel) -> Unit,      //delete

) :
    RecyclerView.Adapter<AffirmationsAdapter.AffirmationViewHolder>() {

    class AffirmationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.affirmationTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.affirmationDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AffirmationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.affirmation_recyclerview_row, parent, false)
        return AffirmationViewHolder(view)
    }

    override fun onBindViewHolder(holder: AffirmationViewHolder, position: Int) {
        val affirmation = affirmations[position]

        // Set text fields
        holder.nameTextView.text = affirmation.text
        holder.dateTextView.text = "Date:  ${formatTimestamp(affirmation.dateAdded)}"

        holder.itemView.setOnClickListener {
            onItemClick(affirmation)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(affirmation)  // Trigger the onItemLongClick callback when item is long-clicked
            true  // Return true to indicate the long-click event has been handled
        }
    }

    fun updateAffirmations(newAffirmations: List<AffirmationModel>) {
        affirmations = newAffirmations
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
        return affirmations.size
    }
}