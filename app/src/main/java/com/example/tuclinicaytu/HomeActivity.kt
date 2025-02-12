package com.example.tuclinicaytu

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Mostrar mensaje de bienvenida
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        welcomeTextView.text = R.string.text_bienvenida.toString()
    }
}