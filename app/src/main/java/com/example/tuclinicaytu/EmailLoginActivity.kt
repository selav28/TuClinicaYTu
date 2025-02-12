package com.example.tuclinicaytu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.example.tuclinicaytu.model.UserInfo
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
    private var userFound = false

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
            signIn()
        }
    }

    private fun signIn() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        findUserByEmail(email)
        if(userFound){
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
        }else{
            Toast.makeText(this, "El usuario no existe", Toast.LENGTH_SHORT).show()
        }

    }

    private fun findUserByEmail(email: String) {
        UserSearchManager.findUsersByEmail(email) { result ->
            when (result) {
                is UserSearchResult.Success -> {
                    if (result.users.isNotEmpty()) {
                        userFound = true
                    } else {
                        userFound = false
                    }
                }
                is UserSearchResult.Error -> {
                    Log.e("EmailLoginActivity", "Error finding user: ${result.message}")
                    userFound = false
                }
            }
        }
    }
}