package com.example.feather.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.feather.ui.theme.FeatherTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeatherTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { innerPadding ->
                        FirestoreUserFirstName(modifier = Modifier.padding(innerPadding))
                    }
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun FirestoreUserFirstName(modifier: Modifier = Modifier) {
    // State to hold the fetched Firestore data
    var firstName by remember { mutableStateOf("Fetching data...") }

    // Fetch Firestore data when the composable is first launched
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val usersCollection = db.collection("users")

        usersCollection.limit(1).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0] // Get the first document
                    firstName = document.getString("firstName") ?: "First name not found"
                } else {
                    firstName = "No users found"
                }
            }
            .addOnFailureListener { exception ->
                firstName = "Failed to fetch data: ${exception.message}"
            }
    }

    // Display the message
    Text(
        text = firstName,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun FirestoreUserFirstNamePreview() {
    FeatherTheme {
        FirestoreUserFirstName()
    }
}