package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_main)

            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, GalleryListFragment())
                    .commit()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // This will help us see what's wrong
            throw e
        }
    }
}