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
import com.example.feather.models.KeywordModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class KeywordsAdapter(
    var keywords: List<KeywordModel>,
    private val onItemClick: (KeywordModel) -> Unit,          //to details
    private val onItemLongClick: (KeywordModel) -> Unit,      //delete

) :
    RecyclerView.Adapter<KeywordsAdapter.KeywordViewHolder>() {

    class KeywordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.keywordTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.keywordDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KeywordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.keyword_recyclerview_row, parent, false)
        return KeywordViewHolder(view)
    }

    override fun onBindViewHolder(holder: KeywordViewHolder, position: Int) {
        val keyword = keywords[position]

        // Set text fields
        holder.nameTextView.text = keyword.name
        holder.dateTextView.text = "Date:  ${formatTimestamp(keyword.dateAdded)}"

        holder.itemView.setOnClickListener {
            onItemClick(keyword)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(keyword)  // Trigger the onItemLongClick callback when item is long-clicked
            true  // Return true to indicate the long-click event has been handled
        }
    }

    fun updateKeywords(newKeywords: List<KeywordModel>) {
        keywords = newKeywords
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
        return keywords.size
    }
}