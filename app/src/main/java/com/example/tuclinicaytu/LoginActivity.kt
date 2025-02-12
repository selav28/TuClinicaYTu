package com.example.tuclinicaytu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {

    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var oneTapClient: SignInClient
    private lateinit var googleSignInButton: MaterialButton
    private lateinit var emailSignInButton: MaterialButton
    private lateinit var anonymousSignInButton: MaterialButton
    private lateinit var goToRegisterTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar el BeginSignInRequest y el OneTapClient
        createSignInRequest()
        oneTapClient = Identity.getSignInClient(this)

        // Inicializar el botón de Google
        googleSignInButton = findViewById(R.id.sign_in_with_google_button)
        googleSignInButton.setOnClickListener {
            signInWithGoogle()
        }

        // Inicializar el botón de correo electrónico
        emailSignInButton = findViewById(R.id.sign_in_with_email_button)
        emailSignInButton.setOnClickListener {
            signInWithEmail()
        }

        // Inicializar el botón anónimo
        anonymousSignInButton = findViewById(R.id.sign_in_anonymously_button)
        anonymousSignInButton.setOnClickListener {
            signInAnonymously()
        }

        // Inicializar el TextView para ir a RegisterActivity
        goToRegisterTextView = findViewById(R.id.goToRegisterTextView)
        goToRegisterTextView.setOnClickListener {
            goToRegister()
        }
    }

    private fun createSignInRequest() {
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()
    }

    private fun signInWithGoogle() {
        val launcher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
                    val idToken = credential.googleIdToken
                    when {
                        idToken != null -> {
                            // Send idToken to server for verification and sign in.
                            Log.d("LoginActivity", "Got ID token.")
                            //Navegar a HomeActivity
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                        }

                        else -> {
                            // Shouldn't arrive here.
                            Log.e("LoginActivity", "No ID token!")
                        }
                    }
                } catch (e: ApiException) {
                    Log.e("LoginActivity", "Error: ${e.message}")
                }
            } else {
                Log.e("LoginActivity", "Error: ${result.resultCode}")
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = oneTapClient.beginSignIn(signInRequest).await()
                val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                launcher.launch(intentSenderRequest)
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error: ${e.message}")
            }
        }
    }

    private fun signInWithEmail() {
        // Iniciar EmailLoginActivity
        val intent = Intent(this, EmailLoginActivity::class.java)
        startActivity(intent)
    }

    private fun signInAnonymously() {
        // Lógica para el inicio de sesión anónimo
        Log.d("LoginActivity", "Inicio de sesión anónimo")
        //Navegar a HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun goToRegister() {
        // Iniciar RegisterActivity
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}