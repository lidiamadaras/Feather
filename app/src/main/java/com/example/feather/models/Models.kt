package com.example.feather.models

import com.google.firebase.Timestamp
import java.util.Date

data class DreamModel(
    val dateAdded: Timestamp?,
    val description: String?,
    val category: String?,
    val hoursSlept: String?,
    val isRecurring: Boolean?,
    val title: String?,
    val keywords: List<String> = emptyList()
)

data class KeywordModel(
    val name: String = "",
    val dateAdded: Timestamp? = null
)

