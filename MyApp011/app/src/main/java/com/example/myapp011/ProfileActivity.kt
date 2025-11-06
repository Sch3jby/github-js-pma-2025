package com.example.myapp011

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp011.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nastavit Action Bar
        supportActionBar?.title = "Profil"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Inicializovat image picker
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                binding.ivAvatar.setImageURI(it)
                saveAvatarUri(it.toString())
            }
        }

        // Načíst uložená data
        loadProfile()

        // Tlačítko pro výběr avatara
        binding.btnSelectAvatar.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Tlačítko pro uložení jména
        binding.btnSaveName.setOnClickListener {
            saveName()
        }

        // Aktualizovat odznaky
        updateBadges()
    }

    private fun loadProfile() {
        val prefs = getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)

        // Načíst jméno
        val userName = prefs.getString("user_name", null)
        if (userName != null) {
            binding.etName.setText(userName)
        }

        // Načíst avatar
        val avatarUri = prefs.getString("avatar_uri", null)
        if (avatarUri != null) {
            binding.ivAvatar.setImageURI(Uri.parse(avatarUri))
        }
    }

    private fun saveName() {
        val name = binding.etName.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Zadej jméno!", Toast.LENGTH_SHORT).show()
            return
        }

        // Uložit do SharedPreferences
        val prefs = getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("user_name", name)
        editor.apply()

        Toast.makeText(this, "✅ Jméno uloženo!", Toast.LENGTH_SHORT).show()

        // Vymazat focus z EditText
        binding.etName.clearFocus()
    }

    private fun saveAvatarUri(uri: String) {
        val prefs = getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("avatar_uri", uri)
        editor.apply()

        Toast.makeText(this, "✅ Avatar uložen!", Toast.LENGTH_SHORT).show()
    }

    private fun updateBadges() {
        val prefs = getSharedPreferences("KvizMistr", Context.MODE_PRIVATE)

        val quizzesPlayed = prefs.getInt("quizzes_played", 0)
        val totalScore = prefs.getInt("total_score", 0)

        // Začátečník - 1 kvíz
        binding.cbBeginner.isChecked = quizzesPlayed >= 1

        // Expert - 10 kvízů
        binding.cbExpert.isChecked = quizzesPlayed >= 10

        // Mistr - 50 bodů
        binding.cbMaster.isChecked = totalScore >= 50
    }

    // Zpět tlačítko v Action Bar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}