package com.example.tuclinicaytu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signOutButton: MaterialButton
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Sign Out button
        signOutButton = findViewById(R.id.signOutButton)
        signOutButton.setOnClickListener {
            signOut()
        }
    }

    private fun signOut() {
        auth.signOut()
        // Navigate to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close HomeActivity
    }
}