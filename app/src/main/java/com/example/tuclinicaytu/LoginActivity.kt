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

    private lateinit var googleSignInButton: MaterialButton
    private lateinit var emailSignInButton: MaterialButton
    private lateinit var anonymousSignInButton: MaterialButton
    private lateinit var goToRegisterTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val TAG = "LoginActivity"
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Google Sign-In button
        googleSignInButton = findViewById(R.id.loginButton)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Initialize Email Sign-In button
        emailSignInButton = findViewById(R.id.emailLoginButton)
        emailSignInButton.setOnClickListener {
            signInWithEmail()
        }

        // Initialize Anonymous Sign-In button
        anonymousSignInButton = findViewById(R.id.anonymousLoginButton)
        anonymousSignInButton.setOnClickListener {
            signInAnonymously()
        }

        // Initialize TextView to go to RegisterActivity
        goToRegisterTextView = findViewById(R.id.registerTextView)
        goToRegisterTextView.setOnClickListener {
            goToRegister()
        }
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data ?: Intent())
                handleSignInResult(task)
            } else {
                Log.e(TAG, "Error: ${result.resultCode}")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: com.google.firebase.auth.FirebaseUser?) {
        if (user != null) {
            // Navigate to HomeActivity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Close LoginActivity
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "Google sign in success")
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun signInWithEmail() {
        // Start EmailLoginActivity
        val intent = Intent(this, EmailLoginActivity::class.java)
        startActivity(intent)
    }

    private fun signInAnonymously() {
        // Logic for anonymous sign-in
        Log.d(TAG, "Anonymous sign-in")
        // Navigate to HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity
    }

    private fun goToRegister() {
        // Start RegisterActivity
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}