package com.example.spiicev2.data.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore(name = "user_preferences")

private val EMAIL_KEY = stringPreferencesKey("user_email")

class DataStoreManager(private val context: Context) {

    suspend fun setEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
        }
    }

    suspend fun getEmail(): String? {
        val preferences = context.dataStore.data.first()
        return preferences[EMAIL_KEY]
    }
}
