package com.example.tuclinicaytu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize UI elements
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val confirmPassword = confirmPasswordEditText.text.toString()

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("RegisterActivity", "createUserWithEmail:success")
                    val user = auth.currentUser
                    //Navegar a HomeActivity
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish() // Cierra la RegisterActivity
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}