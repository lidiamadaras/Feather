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
import com.example.feather.models.DreamInterpretationModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.sql.DataSource

class InterpretationsAdapter(
    var interpretations: List<DreamInterpretationModel>,
    private val onItemClick: (DreamInterpretationModel) -> Unit,          //to details
    private val onItemLongClick: (DreamInterpretationModel) -> Unit,      //delete

) :
    RecyclerView.Adapter<InterpretationsAdapter.InterpretationViewHolder>() {

    class InterpretationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val personaTextView: TextView = itemView.findViewById(R.id.personaTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterpretationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.interpretation_recyclerview_row, parent, false)
        return InterpretationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InterpretationViewHolder, position: Int) {
        val interpretation = interpretations[position]

        holder.nameTextView.text = interpretation.title
        holder.personaTextView.text = "Persona used: ${interpretation.personaGemini}"
        holder.dateTextView.text = "Date:  ${formatTimestamp(interpretation.timeAdded)}"

        holder.itemView.setOnClickListener {
            onItemClick(interpretation)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(interpretation)  // Trigger the onItemLongClick callback when item is long-clicked
            true  // Return true to indicate the long-click event has been handled
        }
    }

    fun updateInterpretations (newInterpretations: List<DreamInterpretationModel>) {
        interpretations = newInterpretations
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
        return interpretations.size
    }
}