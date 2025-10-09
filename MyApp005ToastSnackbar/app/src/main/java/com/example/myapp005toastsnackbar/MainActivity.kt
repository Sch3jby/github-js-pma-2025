package com.example.myapp005toastsnackbar

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Tlačítko pro základní Toast
        findViewById<Button>(R.id.btnSimpleToast).setOnClickListener {
            Toast.makeText(this, "Toto je jednoduchý Toast!", Toast.LENGTH_SHORT).show()
        }

        // Tlačítko pro vlastní Toast s ikonkou
        findViewById<Button>(R.id.btnCustomToast).setOnClickListener {
            showCustomToast()
        }

        // Tlačítko pro dlouhý Toast
        findViewById<Button>(R.id.btnLongToast).setOnClickListener {
            Toast.makeText(this, "Toto je dlouhý Toast s více textem!", Toast.LENGTH_LONG).show()
        }

        // Tlačítko pro jednoduchý Snackbar
        findViewById<Button>(R.id.btnSimpleSnackbar).setOnClickListener { view ->
            Snackbar.make(view, "Toto je jednoduchý Snackbar", Snackbar.LENGTH_SHORT).show()
        }

        // Tlačítko pro Snackbar s akcí
        findViewById<Button>(R.id.btnActionSnackbar).setOnClickListener { view ->
            Snackbar.make(view, "Soubor byl smazán", Snackbar.LENGTH_LONG)
                .setAction("VRÁTIT") {
                    Toast.makeText(this, "Soubor obnoven!", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        // Tlačítko pro dlouhý Snackbar
        findViewById<Button>(R.id.btnLongSnackbar).setOnClickListener { view ->
            Snackbar.make(view, "Toto je delší Snackbar s důležitou zprávou pro uživatele", Snackbar.LENGTH_LONG)
                .setAction("OK") {
                    // Akce po kliknutí
                }
                .setActionTextColor(getColor(android.R.color.holo_orange_light))
                .show()
        }
    }

    private fun showCustomToast() {
        // Vytvoření vlastního Toast layoutu
        val inflater = LayoutInflater.from(this)
        val layout = inflater.inflate(R.layout.custom_toast, null)

        // Nastavení textu a ikonky
        layout.findViewById<TextView>(R.id.toast_text).text = "Vlastní Toast s ikonkou! ✓"
        layout.findViewById<ImageView>(R.id.toast_icon).setImageResource(android.R.drawable.ic_dialog_info)

        // Vytvoření a zobrazení Toast
        Toast(this).apply {
            duration = Toast.LENGTH_LONG
            view = layout
            setGravity(Gravity.CENTER, 0, 0) // Zobrazení uprostřed obrazovky
            show()
        }
    }
}