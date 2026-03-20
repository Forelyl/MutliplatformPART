package ua.kpi.ipze.part.services.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path

class PrefManager : ViewModel() {
    companion object {
        private val LANGUAGE_KEY = stringPreferencesKey("language_preference")
        private const val PREFERENCE_FULL_FILENAME = "settings.preferences_pb"
        const val DEFAULT_LANGUAGE = "en"
        private val dataStore = constructDataStore(PREFERENCE_FULL_FILENAME)
    }


    fun getLanguagePreference(): Flow<String> {
        return dataStore.data
            .map { preferences ->
                preferences[LANGUAGE_KEY] ?: DEFAULT_LANGUAGE
            }
    }

    suspend fun setLanguagePreference(languageCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = languageCode
        }
    }
}

// ---------------------------------------------------------------------
// platform specific

internal fun constructDataStoreFromPath(pathToFile: Path): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { pathToFile }
    )

expect fun constructDataStore(fullFileName: String): DataStore<Preferences>