package com.unscroll.app.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.unscroll.app.data.source.CuratedInsultsDataSource
import com.unscroll.app.domain.model.Insult
import com.unscroll.app.domain.repository.InsultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.insultDataStore: DataStore<Preferences> by preferencesDataStore(name = "unscroll_insults")

class LocalInsultRepositoryImpl(
    private val context: Context
) : InsultRepository {

    private object PreferencesKeys {
        val INSULT_INDEX = intPreferencesKey("current_insult_index")
    }

    override suspend fun getNextInsult(userName: String?): Insult {
        val preferences = context.insultDataStore.data.first()
        val currentIndex = preferences[PreferencesKeys.INSULT_INDEX] ?: 0
        val baseInsult = CuratedInsultsDataSource.getInsult(currentIndex)
        val formattedTemplate = baseInsult.formatForUser(userName)
        return baseInsult.copy(rawTemplate = formattedTemplate)
    }

    override fun getCurrentInsultIndex(): Flow<Int> {
        return context.insultDataStore.data.map { preferences ->
            preferences[PreferencesKeys.INSULT_INDEX] ?: 0
        }
    }

    override suspend fun advanceInsultIndex() {
        context.insultDataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.INSULT_INDEX] ?: 0
            val next = (current + 1) % CuratedInsultsDataSource.INSULTS.size
            preferences[PreferencesKeys.INSULT_INDEX] = next
        }
    }
}
