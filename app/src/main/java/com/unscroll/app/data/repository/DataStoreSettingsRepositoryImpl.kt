package com.unscroll.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.unscroll.app.domain.model.UserProfile
import com.unscroll.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "unscroll_settings")

class DataStoreSettingsRepositoryImpl(
    private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val INTERRUPTION_INTERVAL = intPreferencesKey("interruption_interval")
        val TRACKED_PACKAGES = stringSetPreferencesKey("tracked_packages")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    override fun getUserProfile(): Flow<UserProfile> {
        return context.dataStore.data.map { preferences ->
            val name = preferences[PreferencesKeys.USER_NAME] ?: ""
            val interval = preferences[PreferencesKeys.INTERRUPTION_INTERVAL] ?: 10
            UserProfile(
                userName = name,
                isPersonalized = name.isNotBlank(),
                interruptionIntervalMinutes = interval
            )
        }
    }

    override suspend fun updateUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name.trim()
        }
    }

    override suspend fun updateInterruptionInterval(intervalMinutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.INTERRUPTION_INTERVAL] = intervalMinutes.coerceAtLeast(1)
        }
    }

    override fun getTrackedPackageNames(): Flow<Set<String>> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.TRACKED_PACKAGES] ?: setOf(
                "com.instagram.android",
                "com.google.android.youtube",
                "com.zhiliaoapp.musically",
                "com.ss.android.ugc.trill"
            )
        }
    }

    override suspend fun setTrackedPackageNames(packages: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TRACKED_PACKAGES] = packages
        }
    }

    override suspend fun addTrackedPackage(packageName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TRACKED_PACKAGES] ?: emptySet()
            preferences[PreferencesKeys.TRACKED_PACKAGES] = current + packageName
        }
    }

    override suspend fun removeTrackedPackage(packageName: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TRACKED_PACKAGES] ?: emptySet()
            preferences[PreferencesKeys.TRACKED_PACKAGES] = current - packageName
        }
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }
}
