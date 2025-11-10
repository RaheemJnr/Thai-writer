package com.rjnr.thaiwrter.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_preferences")

data class OnboardingSettings(
        val goal: String = "travel",
        val paceMinutes: Int = 5,
        val confidence: Int = 2,
        val isComplete: Boolean = false
)

class OnboardingPreferences(private val context: Context) {

    private val dataStore
        get() = context.onboardingDataStore

    private object Keys {
        val GOAL = stringPreferencesKey("goal")
        val PACE = intPreferencesKey("pace_minutes")
        val CONFIDENCE = intPreferencesKey("confidence")
        val COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    val settings: Flow<OnboardingSettings> =
            dataStore.data.map { prefs ->
                OnboardingSettings(
                        goal = prefs[Keys.GOAL] ?: "travel",
                        paceMinutes = prefs[Keys.PACE] ?: 5,
                        confidence = prefs[Keys.CONFIDENCE] ?: 2,
                        isComplete = prefs[Keys.COMPLETE] ?: false
                )
            }

    suspend fun updateGoal(goal: String) {
        dataStore.edit { prefs -> prefs[Keys.GOAL] = goal }
    }

    suspend fun updatePace(minutes: Int) {
        dataStore.edit { prefs -> prefs[Keys.PACE] = minutes }
    }

    suspend fun updateConfidence(level: Int) {
        dataStore.edit { prefs -> prefs[Keys.CONFIDENCE] = level }
    }

    suspend fun markComplete() {
        dataStore.edit { prefs -> prefs[Keys.COMPLETE] = true }
    }

    suspend fun reset() {
        dataStore.edit { prefs -> prefs[Keys.COMPLETE] = false }
    }
}
