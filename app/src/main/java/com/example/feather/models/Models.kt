package com.example.feather.models

import com.google.firebase.Timestamp

data class DreamModel(
    val dateAdded: Timestamp?,
    val description: String?,
    val category: String?,
    val hoursSlept: String?,
    val isRecurring: Boolean?,
    val title: String?
)

