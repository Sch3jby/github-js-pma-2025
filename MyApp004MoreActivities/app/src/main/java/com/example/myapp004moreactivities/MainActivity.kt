package com.example.myapp004moreactivities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializace views
        editTextName = findViewById(R.id.editTextName)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)

        // Nastavení onClick listeneru pro tlačítko
        buttonSend.setOnClickListener {
            val name = editTextName.text.toString()
            val message = editTextMessage.text.toString()

            // Vytvoření intentu pro SecondActivity
            val intent = Intent(this, SecondActivity::class.java)

            // Přidání dat do intentu
            intent.putExtra("EXTRA_NAME", name)
            intent.putExtra("EXTRA_MESSAGE", message)

            // Spuštění druhé aktivity
            startActivity(intent)
        }
    }
}