package com.example.feather.ui.adapter

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.text.font.Typeface
import androidx.recyclerview.widget.RecyclerView
import com.example.feather.R
import com.example.feather.models.SymbolModel

class SymbolsAdapter(
    private var symbols: List<SymbolModel>,
    private val onItemClick: (SymbolModel) -> Unit, // Click to details
    private val onGeminiClick: () -> Unit // Click to open Gemini
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SYMBOL = 0
        private const val VIEW_TYPE_NO_MATCH = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (symbols[position].id == "no_match") VIEW_TYPE_NO_MATCH else VIEW_TYPE_SYMBOL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SYMBOL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.symbol_recyclerview_row, parent, false)
            SymbolViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.no_match_layout, parent, false) // Ensure this layout exists
            NoMatchViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SYMBOL) {
            (holder as SymbolViewHolder).bind(symbols[position], onItemClick)
        } else {
            (holder as NoMatchViewHolder).bind(onGeminiClick)
        }
    }

    override fun getItemCount(): Int = symbols.size

    fun updateSymbols(newSymbols: List<SymbolModel>) {
        symbols = newSymbols
        notifyDataSetChanged()
    }

    class SymbolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.symbolNameTextView)

        fun bind(symbol: SymbolModel, onItemClick: (SymbolModel) -> Unit) {
            nameTextView.text = symbol.name
            itemView.setOnClickListener { onItemClick(symbol) }
        }
    }


    class NoMatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(onGeminiClick: () -> Unit) {
            val messageTextView = itemView.findViewById<TextView>(R.id.noMatchMessage)

            // Create the full message with "Gemini" as part of it
            val fullMessage = "No match found for this symbol: try asking "

            // Make "Gemini" underlined and clickable
            val spannableString = SpannableString(fullMessage + "Gemini")

            // Set the "Gemini" text to be underlined
            spannableString.setSpan(UnderlineSpan(), fullMessage.length, spannableString.length, 0)

            // Set the "Gemini" text to be italic
            //spannableString.setSpan(StyleSpan(Typeface.ITALIC), fullMessage.length, spannableString.length, 0)

            // Set the clickable span for "Gemini"
            spannableString.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    onGeminiClick() // Trigger the Gemini action
                }
            }, fullMessage.length, spannableString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Apply the spannable text to the TextView
            messageTextView.text = spannableString
            messageTextView.movementMethod = LinkMovementMethod.getInstance() // Make the link clickable
        }
    }
}
