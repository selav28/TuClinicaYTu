package com.example.tuclinicaytu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // Button for Google Sign-In
    private lateinit var googleSignInButton: MaterialButton
    // Button for Email Sign-In
    private lateinit var emailSignInButton: MaterialButton
    // Button for Anonymous Sign-In
    private lateinit var anonymousSignInButton: MaterialButton
    // TextView to go to the Register screen
    private lateinit var goToRegisterTextView: TextView
    // Firebase Authentication instance
    private lateinit var auth: FirebaseAuth
    // Google Sign-In client
    private lateinit var googleSignInClient: GoogleSignInClient
    // Tag for logs
    private val TAG = "LoginActivity"
    // Launcher for the Google Sign-In activity
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Center the title in the ActionBar
        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.center_title_actionbar)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize the Google Sign-In button
        googleSignInButton = findViewById(R.id.loginButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle() // Sign in with Google
        }

        // Initialize the Email Sign-In button
        emailSignInButton = findViewById(R.id.emailLoginButton)
        emailSignInButton.setOnClickListener {
            signInWithEmail() // Sign in with Email
        }

        // Initialize the Anonymous Sign-In button
        anonymousSignInButton = findViewById(R.id.anonymousLoginButton)
        anonymousSignInButton.setOnClickListener {
            signInAnonymously() // Sign in Anonymously
        }

        // Initialize the TextView to go to the Register activity
        goToRegisterTextView = findViewById(R.id.registerTextView)
        goToRegisterTextView.setOnClickListener {
            goToRegister() // Go to the Register activity
        }
        // Configure the launcher for the Google Sign-In activity
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // If the result is OK, get the Google account
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data ?: Intent())
                handleSignInResult(task) // Handle the Sign-In result
            } else {
                // If there is an error, show a message in the log
                Log.e(TAG, "Error: ${result.resultCode}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if the user is already signed in
        val currentUser = auth.currentUser
        updateUI(currentUser) // Update the user interface
    }

    // Update the user interface according to the user's status
    private fun updateUI(user: com.google.firebase.auth.FirebaseUser?) {
        if (user != null) {
            // If the user is signed in, navigate to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Close LoginActivity
        }
    }

    // Sign in with Google
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent) // Launch the Google Sign-In activity
    }

    // Handle the result of the Google Sign-In
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // If the sign-in is successful, show the authenticated user interface
            Log.d(TAG, "Google sign in success")
            firebaseAuthWithGoogle(account.idToken!!) // Authenticate with Firebase
        } catch (e: ApiException) {
            // If there is an error, show a message in the log
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    // Authenticate with Firebase using the Google token
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // If the sign-in is successful, update the user interface
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user) // Update the user interface
                } else {
                    // If there is an error, show a message in the log
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null) // Update the user interface
                }
            }
    }

    // Sign in with Email
    private fun signInWithEmail() {
        // Start EmailLoginActivity
        val intent = Intent(this, EmailLoginActivity::class.java)
        startActivity(intent)
    }

    // Sign in Anonymously
    private fun signInAnonymously() {
        // Logic for anonymous sign-in
        Log.d(TAG, "Anonymous sign-in")
        // Navigate to HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity
    }

    // Go to the Register activity
    private fun goToRegister() {
        // Start RegisterActivity
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}