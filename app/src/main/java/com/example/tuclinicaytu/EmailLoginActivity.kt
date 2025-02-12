package com.example.tuclinicaytu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class EmailLoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_login)
        // Centrar el título en la ActionBar
        supportActionBar?.displayOptions = androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.center_title_actionbar)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize UI elements
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, continue with checking user provider
                        Log.d("EmailLoginActivity", "signInAnonymously:success")
                        checkUserProviderAndSignIn(email, password)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("EmailLoginActivity", "signInAnonymously:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun checkUserProviderAndSignIn(email: String, password: String) {
        UserSearchManager.findUsersByEmail(email) { result ->
            when (result) {
                is UserSearchResult.Success -> {
                    if (result.users.isNotEmpty()) {
                        val user = result.users[0] // Tomamos el primer usuario (debería haber solo uno)
                        if (user.providers.contains("google.com")) {
                            // El usuario se registró con Google
                            Toast.makeText(this, "Este usuario se registró con Google. Por favor, inicia sesión con Google.", Toast.LENGTH_LONG).show()
                        } else {
                            // El usuario no se registró con Google, intentamos el inicio de sesión con email y password
                            signInWithEmailAndPassword(email, password)
                        }
                    } else {
                        // El usuario no existe, intentamos el inicio de sesión con email y password
                        signInWithEmailAndPassword(email, password)
                    }
                }
                is UserSearchResult.Error -> {
                    Log.e("EmailLoginActivity", "Error finding user: ${result.message}")
                    Toast.makeText(this, "Error al buscar el usuario", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("EmailLoginActivity", "signInWithEmail:success")
                    val user = auth.currentUser
                    //Navegar a HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra la EmailLoginActivity
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("EmailLoginActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}