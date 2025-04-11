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

class DreamRepository  @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun saveDream(dream: DreamModel): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val dreamRef =
                    db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams")
                    .document()

                val dreamFields = mapOf(
                    "dateAdded" to Timestamp.now(), // Automatically get the current timestamp
                    "description" to dream.description,
                    "category" to dream.category,
                    "hoursSlept" to dream.hoursSlept,
                    "isRecurring" to dream.isRecurring,
                    "title" to dream.title,
                    "keywords" to dream.keywords,
                    "symbols" to dream.symbols
                )

                dreamRef.set(dreamFields).await()

                Result.success(Unit)
            }
            else {return Result.failure(Exception("User not authenticated"))}
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveKeyword(keyword: KeywordModel): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords") // Store keywords globally for the user
                    .document(keyword.name) // Use keyword name as the document ID to prevent duplicates

                val snapshot = keywordRef.get().await()
                if (snapshot.exists()) {
                    return Result.failure(Exception("Keyword already exists!"))
                }

                // Perform the write operation
                keywordRef.set(keyword).await()

                Result.success(Unit)
            } else {
                Result.failure(Exception("User not authenticated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserKeywords(): List<KeywordModel> {
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords") // Global keyword list
                    .orderBy("dateAdded", Query.Direction.DESCENDING)


                val snapshot = keywordsRef.get().await()

                val keywords = snapshot.documents.mapNotNull { it.toObject(KeywordModel::class.java) }
                return keywords
            } else {
                Log.d("keyword", "there are no keywords when fetching from repo")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("KeywordRepository", "Error fetching keywords: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getUserDreams(): List<DreamModel>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val dreamsRef = db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams") // Global keyword list
                    .orderBy("dateAdded", Query.Direction.DESCENDING)

                val snapshot = dreamsRef.get().await()

                snapshot.documents.mapNotNull { document ->
                    document.toObject(DreamModel::class.java)?.copy(id = document.id) // Store Firestore ID
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("DreamRepo", it) }
            emptyList()
        }
    }

    suspend fun deleteDream(dreamId: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams")
                    .document(dreamId)
                    .delete()
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error deleting dream: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getDreamById(dreamId: String): DreamModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val dreamDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("dreams")
                    .document(dreamId)
                    .get()
                    .await()

                if (dreamDoc.exists()) {
                    dreamDoc.toObject(DreamModel::class.java)?.copy(id = dreamDoc.id)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error fetching dream: ${e.message}")
            null
        }

    }

    //keywords:

    suspend fun getKeywordById(keywordId: String): KeywordModel?{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val keywordDoc = db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords")
                    .document(keywordId)
                    .get()
                    .await()

                if (keywordDoc.exists()) {
                    keywordDoc.toObject(KeywordModel::class.java)
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error fetching keyword: ${e.message}")
            null
        }

    }

    suspend fun deleteKeyword(keywordId: String): Result<Unit>{
        return try {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("keywords")
                    .document(keywordId)
                    .delete()
                    .await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not logged in"))
            }
        } catch (e: Exception) {
            Log.e("DreamRepo", "Error deleting keyword: ${e.message}")
            Result.failure(e)
        }
    }

    fun fetchDataFromFirestore(): Map<String, CalendarDay> {
        val logMap = mutableMapOf<String, CalendarDay>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentUser = auth.currentUser

        fun put(dateKey: String, update: (CalendarDay) -> CalendarDay) {
            val old = logMap[dateKey] ?: CalendarDay()
            logMap[dateKey] = update(old)
        }

        val collections = listOf("dreams", "feelings", "reflections", "affirmations")

        collections.forEach { collection ->
            if (currentUser != null) {
                db.collection("users").document(currentUser.uid).collection(collection)
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val timestamp = document.getTimestamp("dateAdded")
                            if (timestamp != null) {
                                val dateKey = timestamp.toDate().let { dateFormat.format(it) }

                                // Check the collection type and add to the appropriate log map
                                when (collection) {
                                    "dreams" -> put(dateKey) { it.copy(hasDream = true) }
                                    "feelings" -> put(dateKey) { it.copy(hasFeeling = true) }
                                    "reflections" -> put(dateKey) { it.copy(hasReflection = true) }
                                    "affirmations" -> put(dateKey) { it.copy(hasAffirmation = true) }
                                }
                            }
                        }
                    }
            }
        }

        return logMap
    }

    fun generateCalendarDays(): List<CalendarDay> {

        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentYear = calendar.get(Calendar.YEAR)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1

        val totalCells = 42  // A 6-week calendar
        val dayList = mutableListOf<CalendarDay>()

        val logMap = fetchDataFromFirestore()

        repeat(firstDayOfWeek) {
            dayList.add(CalendarDay())
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (day in 1..daysInMonth) {
            calendar.set(Calendar.DAY_OF_MONTH, day)
            val dateKey = dateFormat.format(calendar.time)

            val base = logMap[dateKey] ?: CalendarDay()
            val enriched = base.copy(dayNumber = day)
            dayList.add(enriched)
        }

        while (dayList.size < totalCells) {
            dayList.add(CalendarDay())
        }

        return dayList
    }

}
