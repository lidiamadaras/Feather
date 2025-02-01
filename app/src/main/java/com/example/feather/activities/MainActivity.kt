package com.example.feather.activities

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.feather.R
import com.example.feather.databinding.ActivityMainBinding
import com.example.feather.ui.theme.FeatherTheme
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity()  {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout and set the content view
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //Redirect to AuthActivity from MainActivity if Not Logged In:

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        // Retrieve the NavController from the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.selectedItemId = R.id.homeFragment

        // Set up the BottomNavigationView with the NavController
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.dreamAnalysisFragment -> {
                    navController.navigate(R.id.dreamAnalysisFragment)
                    true
                }
                R.id.exploreFragment -> {
                    navController.navigate(R.id.exploreFragment)
                    true
                }
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.statsFragment -> {
                    navController.navigate(R.id.statsFragment)
                    true
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
        //navigate to Home when authentication is done:

        navController.navigate(R.id.homeFragment)
    }
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