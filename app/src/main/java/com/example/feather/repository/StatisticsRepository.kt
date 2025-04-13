package com.example.feather.repository

import android.util.Log
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.feather.models.CalendarDay
import com.example.feather.models.DreamModel
import com.example.feather.models.KeywordModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

//for handling all firestore operations: repo layer

class StatisticsRepository  @Inject constructor() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()

    suspend inline fun <reified T> getLogsForDay(
        collectionName: String,
        day: Int,
        month: Int,
        year: Int
    ): List<T> {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("StatisticsRepository", "User not logged in.")
            return emptyList()
        }

        return try {
            val calendarStart = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            Log.d("StatisticsRepository", "Calendar start: $calendarStart")

            val calendarEnd = Calendar.getInstance().apply {
                time = calendarStart.time
                add(Calendar.DAY_OF_MONTH, 1)
            }

            Log.d("StatisticsRepository", "Calendar end: $calendarStart")

            val startTimestamp = Timestamp(calendarStart.time)
            val endTimestamp = Timestamp(calendarEnd.time)

            Log.d("StatisticsRepository", "start timestamp: $startTimestamp, end time stamp: $endTimestamp")

            val snapshot = db.collection("users")
                .document(userId)
                .collection(collectionName)
                .whereGreaterThanOrEqualTo("dateAdded", startTimestamp)
                .whereLessThan("dateAdded", endTimestamp)
                .get()
                .await()

            Log.d("StatisticsRepository", "snapshot: $snapshot")
            val temp = snapshot.toObjects(T::class.java)
            Log.d("StatisticsRepository", "temp: $temp")
            return temp
        } catch (e: Exception) {
            Log.e("StatisticsRepository", "Error fetching $collectionName logs: ${e.message}")
            emptyList()
        }
    }
}
