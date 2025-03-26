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
import com.example.feather.models.SymbolModel
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class SymbolsAdapter(
    var symbols: List<SymbolModel>,
    private val onItemClick: (SymbolModel) -> Unit,          //to details
) :
    RecyclerView.Adapter<SymbolsAdapter.SymbolViewHolder>() {

    class SymbolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.symbolNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymbolViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.symbol_recyclerview_row, parent, false)
        return SymbolViewHolder(view)
    }

    override fun onBindViewHolder(holder: SymbolViewHolder, position: Int) {
        val symbol = symbols[position]

        // Set text fields
        holder.nameTextView.text = symbol.name

        holder.itemView.setOnClickListener {
            onItemClick(symbol)
        }
    }

    fun updateSymbols(newSymbols: List<SymbolModel>) {
        symbols = newSymbols
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun getItemCount(): Int {
        return symbols.size
    }
}