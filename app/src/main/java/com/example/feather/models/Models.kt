package com.example.feather.models

import com.google.firebase.Timestamp

data class UserData(
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val email: String = "",
    val preferredPersona: String = ""
) {
    constructor() : this("", "", "", "")
}


data class DreamModel(
    val id: String = "",
    val dateAdded: Timestamp? = null,
    val description: String = "",
    val category: String = "",
    val hoursSlept: String = "",
    val isRecurring: Boolean = false,
    val title: String = "",
    val keywords: List<String> = emptyList()
)

data class AffirmationModel(
    val id: String = "",
    val dateAdded: Timestamp? = null,
    val text: String = ""
)

data class ReflectionModel(
    val id: String = "",
    val dateAdded: Timestamp? = null,
    val text: String = "",
)

data class KeywordModel(
    val name: String = "",
    val dateAdded: Timestamp? = null
)

data class FeelingModel(
    val id: String = "",
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

data class SymbolModel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val searchCount: Int = 0,
    val tag : String = "General"
)

data class AIPersonaModel(
    val name: String = "Psychological",
    val dateAdded: Timestamp? = null,
    val description: String? = null,
    val prompt: String? = null
)
