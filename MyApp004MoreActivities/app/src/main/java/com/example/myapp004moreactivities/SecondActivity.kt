package com.example.myapp004moreactivities

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SecondActivity : AppCompatActivity() {

    private lateinit var textViewName: TextView
    private lateinit var textViewMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Inicializace views
        textViewName = findViewById(R.id.textViewName)
        textViewMessage = findViewById(R.id.textViewMessage)

        // Získání dat z intentu
        val name = intent.getStringExtra("EXTRA_NAME") ?: "Neznámý"
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Žádná zpráva"

        // Zobrazení dat
        textViewName.text = "Jméno: $name"
        textViewMessage.text = "Zpráva: $message"
    }
}