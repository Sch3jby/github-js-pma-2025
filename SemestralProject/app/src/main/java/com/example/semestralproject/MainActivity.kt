package com.example.semestralproject

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.semestralproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Sportovní deník"

        setupBottomNavigation()

        // Zobrazíme první fragment (Tréninky)
        if (savedInstanceState == null) {
            loadFragment(WorkoutsFragment())
            binding.bottomNavigation.selectedItemId = R.id.navigation_workouts
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_workouts -> {
                    loadFragment(WorkoutsFragment())
                    supportActionBar?.title = "Moje tréninky"
                    true
                }
                R.id.navigation_calendar -> {
                    loadFragment(CalendarFragment())
                    supportActionBar?.title = "Kalendář"
                    true
                }
                R.id.navigation_statistics -> {
                    loadFragment(StatisticsFragment())
                    supportActionBar?.title = "Statistiky"
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    supportActionBar?.title = "Profil"
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, SettingsFragment())
                    .addToBackStack(null)
                    .commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}