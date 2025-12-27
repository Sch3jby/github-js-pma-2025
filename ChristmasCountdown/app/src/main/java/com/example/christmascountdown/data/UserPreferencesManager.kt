package com.example.christmascountdown.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property pro vytvoření DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Manager pro práci s uživatelskými preferencemi pomocí DataStore
 */
object UserPreferencesManager {

    // Definice klíčů
    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    private val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")

    /**
     * Uloží jméno uživatele
     */
    suspend fun saveUserName(context: Context, name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = name
        }
    }

    /**
     * Získá jméno uživatele jako Flow
     */
    fun getUserName(context: Context): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_USER_NAME] ?: ""
        }
    }

    /**
     * Nastaví, zda jsou notifikace povolené
     */
    suspend fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    /**
     * Získá stav notifikací jako Flow
     */
    fun getNotificationsEnabled(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
        }
    }

    /**
     * Nastaví, že aplikace už byla spuštěna
     */
    suspend fun setFirstLaunchComplete(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FIRST_LAUNCH] = false
        }
    }

    /**
     * Zkontroluje, zda je to první spuštění
     */
    fun isFirstLaunch(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[KEY_FIRST_LAUNCH] ?: true
        }
    }
}