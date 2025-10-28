package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Přidáme ListFragment, pokud ještě neexistuje
        if (savedInstanceState == null) {
            val listFragment = ListFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_list, listFragment)
                .commit()
        }
    }

    // Voláno při výběru fotografie
    fun onPhotoSelected(photo: Photo) {
        // Vytvoření nového DetailFragment a nastavení argumentů
        val detailFragment = DetailFragment()

        val bundle = Bundle().apply {
            putString("title", photo.title)
            putString("author", photo.author)
            putString("description", photo.description)
            putString("location", photo.location)
            putInt("imageResource", photo.imageResource)
            putInt("likes", photo.likes)
        }
        detailFragment.arguments = bundle

        // Nahradíme starý fragment novým
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_detail, detailFragment)
            .commit()
    }
}