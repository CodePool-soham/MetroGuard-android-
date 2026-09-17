package com.example.metroguardai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.metroguardai.domain.model.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("jwt_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val ROLE_KEY = stringPreferencesKey("role")
        private val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    }

    val userSession: Flow<UserSession> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserSession(
                token = preferences[TOKEN_KEY],
                userId = preferences[USER_ID_KEY],
                email = preferences[EMAIL_KEY],
                name = preferences[NAME_KEY],
                role = preferences[ROLE_KEY]
            )
        }

    val themeMode: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[THEME_MODE_KEY] ?: "system"
        }

    suspend fun saveThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = mode
        }
    }

    suspend fun saveSession(session: UserSession) {
        context.dataStore.edit { preferences ->
            session.token?.let { preferences[TOKEN_KEY] = it }
            session.userId?.let { preferences[USER_ID_KEY] = it }
            session.email?.let { preferences[EMAIL_KEY] = it }
            session.name?.let { preferences[NAME_KEY] = it }
            session.role?.let { preferences[ROLE_KEY] = it }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
