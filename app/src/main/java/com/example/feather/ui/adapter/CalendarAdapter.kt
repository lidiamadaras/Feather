package com.example.feather.ui.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.feather.models.CalendarDay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.feather.R
import  com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import javax.sql.DataSource

class CalendarAdapter(private val days: List<CalendarDay>) :
    RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayNumberText: TextView = itemView.findViewById(R.id.dayNumberText)
        val dotContainer: LinearLayout = itemView.findViewById(R.id.dotContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        holder.dotContainer.removeAllViews()

        if (day.dayNumber != null) {
            holder.dayNumberText.text = day.dayNumber.toString()
        } else {
            holder.dayNumberText.text = ""
        }

        val context = holder.itemView.context

        fun addDot(color: Int) {
            val dot = View(context).apply {
                layoutParams = LinearLayout.LayoutParams(10, 10).apply {
                    setMargins(4, 0, 4, 0)
                }
                setBackgroundResource(R.drawable.dot_circle)
                background.setTint(ContextCompat.getColor(context, color))
            }
            holder.dotContainer.addView(dot)
        }

        if (day.hasDream) addDot(R.color.dreamDot)
        if (day.hasFeeling) addDot(R.color.feelingDot)
        if (day.hasReflection) addDot(R.color.reflectionDot)
        if (day.hasAffirmation) addDot(R.color.affirmationDot)
    }

    override fun getItemCount() = days.size
}
