package com.example.feather.models

import com.google.firebase.Timestamp
import java.util.Date

data class DreamModel(
    val dateAdded: Timestamp? = null,
    val description: String = "",
    val category: String = "",
    val hoursSlept: String = "",
    val isRecurring: Boolean = false,
    val title: String = "",
    val keywords: List<String> = emptyList()
)

data class AffirmationModel(
    val dateAdded: Timestamp? = null,
    val text: String = "",
)

data class KeywordModel(
    val name: String = "",
    val dateAdded: Timestamp? = null
)

data class FeelingModel(
    val dateAdded: Timestamp? = null,
    val timeStarted: String = "",   //user can add when they started feeling this way
    val timeEnded : String = "",   //user can add when they stopped feeling this way
    val intensity: String = "",
    val emotion: String = ""              //selected from user's saved emotions
)

//similar to keywords in dreams, you add emotions to a feeling log from user's saved emotions:
data class EmotionModel(
    val name: String = "",
    val dateAdded: Timestamp? = null,
    val description: String? = null
)
